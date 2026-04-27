package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.Reservation;
import com.library.entity.User;
import com.library.entity.enums.BookCopyStatus;
import com.library.entity.enums.BorrowStatus;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private FlowLogService flowLogService;

    @Autowired
    private CompensationService compensationService;

    @Value("${library.config.max-borrow-days:30}")
    private int maxBorrowDays;

    @Value("${library.config.max-renew-days:15}")
    private int maxRenewDays;

    public List<BorrowRecord> findAll() {
        return borrowRecordRepository.findAll();
    }

    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    public List<BorrowRecord> findByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }

    public List<BorrowRecord> findActiveByUserId(Long userId) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.BORROWED);
    }

    public int getCurrentBorrowCount(Long userId) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.BORROWED).size();
    }

    public BorrowRecord borrowBook(Long userId, Long bookId) throws Exception {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("用户不存在");
        }
        User user = userOpt.get();

        if (userService.isBlacklisted(userId)) {
            throw new Exception("用户在黑名单中，无法借阅");
        }

        if (compensationService.hasPendingCompensation(userId)) {
            throw new Exception("您有未处理的图书赔偿，请先完成赔偿后再借阅");
        }

        int currentBorrowCount = getCurrentBorrowCount(userId);
        if (currentBorrowCount >= user.getMaxBorrowCount()) {
            throw new Exception("已达到最大借阅数量: " + user.getMaxBorrowCount());
        }

        Optional<Book> bookOpt = bookService.findById(bookId);
        if (!bookOpt.isPresent()) {
            throw new Exception("图书不存在");
        }
        Book book = bookOpt.get();

        Optional<BookCopy> availableCopy = bookService.findAvailableCopy(bookId);
        if (!availableCopy.isPresent()) {
            throw new Exception("该图书暂无可用副本");
        }
        BookCopy bookCopy = availableCopy.get();

        bookService.updateCopyStatus(bookCopy.getId(), BookCopyStatus.BORROWED);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookCopyId(bookCopy.getId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(maxBorrowDays));
        record.setStatus(BorrowStatus.BORROWED);
        record.setRenewCount(0);
        record.setCreatedAt(LocalDateTime.now());
        
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        flowLogService.logBorrow(userId, book.getTitle(), savedRecord.getId());

        return savedRecord;
    }

    public BorrowRecord returnBook(Long borrowRecordId, boolean isDamaged) throws Exception {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowRecordId);
        if (!recordOpt.isPresent()) {
            throw new Exception("借阅记录不存在");
        }
        BorrowRecord record = recordOpt.get();

        if (record.getStatus() != BorrowStatus.BORROWED) {
            throw new Exception("该借阅记录不是借阅中状态");
        }

        Optional<Book> bookOpt = bookService.findById(record.getBookId());
        if (!bookOpt.isPresent()) {
            throw new Exception("关联的图书不存在");
        }
        Book book = bookOpt.get();

        record.setStatus(isDamaged ? BorrowStatus.DAMAGED : BorrowStatus.RETURNED);
        record.setReturnTime(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        if (isDamaged) {
            bookService.updateCopyStatus(record.getBookCopyId(), BookCopyStatus.DAMAGED);
            compensationService.createCompensationForDamagedReturn(borrowRecordId);
        } else {
            bookService.updateCopyStatus(record.getBookCopyId(), BookCopyStatus.AVAILABLE);
            checkAndAssignToReservation(record.getBookId());
        }

        flowLogService.logReturn(record.getUserId(), book.getTitle(), record.getId());

        return savedRecord;
    }

    public void checkAndAssignToReservation(Long bookId) {
        Optional<Reservation> firstReservation = reservationService.findFirstPendingReservation(bookId);
        if (firstReservation.isPresent()) {
            Reservation reservation = firstReservation.get();
            Optional<BookCopy> availableCopy = bookService.findAvailableCopy(bookId);
            if (availableCopy.isPresent()) {
                try {
                    autoBorrowFromReservation(reservation, availableCopy.get());
                } catch (Exception e) {
                    System.err.println("预约自动分配失败: " + e.getMessage());
                }
            }
        }
    }

    private void autoBorrowFromReservation(Reservation reservation, BookCopy bookCopy) throws Exception {
        Long userId = reservation.getUserId();
        Long bookId = reservation.getBookId();

        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            reservationService.cancelReservation(reservation.getId());
            throw new Exception("用户不存在，取消预约");
        }
        User user = userOpt.get();

        if (userService.isBlacklisted(userId)) {
            reservationService.cancelReservation(reservation.getId());
            throw new Exception("用户在黑名单中，取消预约");
        }

        if (compensationService.hasPendingCompensation(userId)) {
            throw new Exception("用户有未处理的图书赔偿，跳过本次分配");
        }

        int currentBorrowCount = getCurrentBorrowCount(userId);
        if (currentBorrowCount >= user.getMaxBorrowCount()) {
            throw new Exception("用户已达到最大借阅数量，跳过本次分配");
        }

        Optional<Book> bookOpt = bookService.findById(bookId);
        if (!bookOpt.isPresent()) {
            reservationService.cancelReservation(reservation.getId());
            throw new Exception("图书不存在，取消预约");
        }
        Book book = bookOpt.get();

        bookService.updateCopyStatus(bookCopy.getId(), BookCopyStatus.BORROWED);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookCopyId(bookCopy.getId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(maxBorrowDays));
        record.setStatus(BorrowStatus.BORROWED);
        record.setRenewCount(0);
        record.setCreatedAt(LocalDateTime.now());

        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        reservationService.assignReservation(reservation.getId());

        flowLogService.logAutoAssign(userId, book.getTitle(), savedRecord.getId(), reservation.getId());
    }

    public BorrowRecord renewBook(Long borrowRecordId) throws Exception {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowRecordId);
        if (!recordOpt.isPresent()) {
            throw new Exception("借阅记录不存在");
        }
        BorrowRecord record = recordOpt.get();

        if (record.getStatus() != BorrowStatus.BORROWED) {
            throw new Exception("该借阅记录不是借阅中状态");
        }

        boolean hasReservation = reservationService.hasPendingReservation(record.getBookId());
        if (hasReservation) {
            throw new Exception("该图书有人预约，无法续借");
        }

        Optional<Book> bookOpt = bookService.findById(record.getBookId());
        if (!bookOpt.isPresent()) {
            throw new Exception("关联的图书不存在");
        }
        Book book = bookOpt.get();

        record.setDueTime(record.getDueTime().plusDays(maxRenewDays));
        record.setStatus(BorrowStatus.RENEWED);
        record.setRenewCount(record.getRenewCount() + 1);
        record.setUpdatedAt(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        flowLogService.logRenew(record.getUserId(), book.getTitle(), record.getId());

        return savedRecord;
    }

    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordRepository.findByDueTimeBefore(LocalDateTime.now()).stream()
                .filter(r -> r.getStatus() == BorrowStatus.BORROWED)
                .collect(java.util.stream.Collectors.toList());
    }
}