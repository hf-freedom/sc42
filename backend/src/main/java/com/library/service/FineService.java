package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Fine;
import com.library.entity.User;
import com.library.entity.enums.FineStatus;
import com.library.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private FlowLogService flowLogService;

    @Value("${library.config.daily-fine:1.0}")
    private double dailyFine;

    @Value("${library.config.max-fine-days:7}")
    private int maxFineDays;

    @Value("${library.config.max-overdue-times:3}")
    private int maxOverdueTimes;

    public List<Fine> findAll() {
        return fineRepository.findAll();
    }

    public Optional<Fine> findById(Long id) {
        return fineRepository.findById(id);
    }

    public List<Fine> findByUserId(Long userId) {
        return fineRepository.findByUserId(userId);
    }

    public List<Fine> findPendingByUserId(Long userId) {
        return fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING);
    }

    public boolean hasPendingFine(Long userId) {
        return !fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING).isEmpty();
    }

    public Fine createFine(Long borrowRecordId) throws Exception {
        Optional<BorrowRecord> recordOpt = borrowService.findById(borrowRecordId);
        if (!recordOpt.isPresent()) {
            throw new Exception("借阅记录不存在");
        }
        BorrowRecord record = recordOpt.get();

        Optional<Fine> existingFine = fineRepository.findByBorrowRecordIdAndStatus(borrowRecordId, FineStatus.PENDING);
        if (existingFine.isPresent()) {
            throw new Exception("该借阅记录已存在未支付的罚金");
        }

        int overdueDays = calculateOverdueDays(record);
        if (overdueDays <= 0) {
            throw new Exception("该借阅记录未逾期");
        }

        double amount = overdueDays * dailyFine;

        Fine fine = new Fine();
        fine.setUserId(record.getUserId());
        fine.setBorrowRecordId(borrowRecordId);
        fine.setAmount(amount);
        fine.setOverdueDays(overdueDays);
        fine.setStatus(FineStatus.PENDING);
        fine.setDueDate(LocalDate.now().plusDays(maxFineDays));
        fine.setCreatedAt(LocalDateTime.now());

        Fine savedFine = fineRepository.save(fine);

        userService.incrementOverdueCount(record.getUserId());

        checkAndAddToBlacklist(record.getUserId());

        Optional<Book> bookOpt = bookService.findById(record.getBookId());
        bookOpt.ifPresent(book -> flowLogService.logFine(record.getUserId(), book.getTitle(), amount, savedFine.getId()));

        return savedFine;
    }

    private int calculateOverdueDays(BorrowRecord record) {
        LocalDateTime dueTime = record.getDueTime();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(dueTime)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(dueTime.toLocalDate(), now.toLocalDate());
    }

    private void checkAndAddToBlacklist(Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getOverdueCount() >= maxOverdueTimes) {
                userService.addToBlacklist(userId, "逾期次数达到" + maxOverdueTimes + "次");
            }
        }
    }

    public void payFine(Long fineId) throws Exception {
        Optional<Fine> fineOpt = fineRepository.findById(fineId);
        if (!fineOpt.isPresent()) {
            throw new Exception("罚金记录不存在");
        }
        Fine fine = fineOpt.get();

        if (fine.getStatus() != FineStatus.PENDING) {
            throw new Exception("该罚金不可支付");
        }

        Long userId = fine.getUserId();
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("用户不存在");
        }
        User user = userOpt.get();

        Double balance = user.getDepositBalance() != null ? user.getDepositBalance() : 0.0;
        if (balance < fine.getAmount()) {
            throw new Exception("押金余额不足，当前余额: " + balance + ", 所需金额: " + fine.getAmount());
        }

        userService.updateDeposit(userId, -fine.getAmount());

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDate.now());
        fine.setUpdatedAt(LocalDateTime.now());
        fineRepository.save(fine);

        flowLogService.logPayFine(fine.getUserId(), fine.getAmount(), fineId);
    }

    public void processOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowService.findOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            try {
                Optional<Fine> existingFine = fineRepository.findByBorrowRecordIdAndStatus(record.getId(), FineStatus.PENDING);
                if (!existingFine.isPresent()) {
                    createFine(record.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkOverdueFinesForBlacklist() {
        LocalDate now = LocalDate.now();
        List<Fine> overdueFines = fineRepository.findByDueDateBefore(now).stream()
                .filter(f -> f.getStatus() == FineStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());

        for (Fine fine : overdueFines) {
            Optional<User> userOpt = userService.findById(fine.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getBlacklistStatus() != com.library.entity.enums.BlacklistStatus.BLACKLISTED) {
                    userService.addToBlacklist(fine.getUserId(), "罚金逾期未支付超过" + maxFineDays + "天");
                }
            }
        }
    }
}