package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.BorrowRecord;
import com.library.entity.Compensation;
import com.library.entity.User;
import com.library.entity.enums.BookCopyStatus;
import com.library.entity.enums.CompensationStatus;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.CompensationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompensationService {

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private FlowLogService flowLogService;

    @Autowired
    private UserService userService;

    public List<Compensation> findAll() {
        return compensationRepository.findAll();
    }

    public Optional<Compensation> findById(Long id) {
        return compensationRepository.findById(id);
    }

    public List<Compensation> findByUserId(Long userId) {
        return compensationRepository.findByUserId(userId);
    }

    public List<Compensation> findPendingByUserId(Long userId) {
        return compensationRepository.findByUserIdAndStatus(userId, CompensationStatus.PENDING);
    }

    public boolean hasPendingCompensation(Long userId) {
        return !compensationRepository.findByUserIdAndStatus(userId, CompensationStatus.PENDING).isEmpty();
    }

    public Compensation createCompensation(Long borrowRecordId, String reason, Double amount) throws Exception {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowRecordId);
        if (!recordOpt.isPresent()) {
            throw new Exception("借阅记录不存在");
        }
        BorrowRecord record = recordOpt.get();

        Optional<Compensation> existingComp = compensationRepository.findByBorrowRecordIdAndStatus(borrowRecordId, CompensationStatus.PENDING);
        if (existingComp.isPresent()) {
            throw new Exception("该借阅记录已存在未处理的赔偿");
        }

        Compensation compensation = new Compensation();
        compensation.setUserId(record.getUserId());
        compensation.setBorrowRecordId(borrowRecordId);
        compensation.setBookCopyId(record.getBookCopyId());
        compensation.setAmount(amount);
        compensation.setReason(reason);
        compensation.setStatus(CompensationStatus.PENDING);
        compensation.setCreatedAt(LocalDateTime.now());

        Compensation savedComp = compensationRepository.save(compensation);

        Optional<Book> bookOpt = bookService.findById(record.getBookId());
        bookOpt.ifPresent(book -> flowLogService.logCompensation(record.getUserId(), book.getTitle(), amount, savedComp.getId()));

        return savedComp;
    }

    public void payCompensation(Long compensationId) throws Exception {
        Optional<Compensation> compOpt = compensationRepository.findById(compensationId);
        if (!compOpt.isPresent()) {
            throw new Exception("赔偿记录不存在");
        }
        Compensation compensation = compOpt.get();

        if (compensation.getStatus() != CompensationStatus.PENDING) {
            throw new Exception("该赔偿不可支付");
        }

        Long userId = compensation.getUserId();
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("用户不存在");
        }
        User user = userOpt.get();

        Double balance = user.getDepositBalance() != null ? user.getDepositBalance() : 0.0;
        if (balance < compensation.getAmount()) {
            throw new Exception("押金余额不足，当前余额: " + balance + ", 所需金额: " + compensation.getAmount());
        }

        userService.updateDeposit(userId, -compensation.getAmount());

        compensation.setStatus(CompensationStatus.PAID);
        compensation.setPaidDate(LocalDateTime.now());
        compensationRepository.save(compensation);

        Optional<BookCopy> copyOpt = bookService.findCopyById(compensation.getBookCopyId());
        if (copyOpt.isPresent()) {
            BookCopy copy = copyOpt.get();
            if (copy.getStatus() == BookCopyStatus.DAMAGED) {
                bookService.updateCopyStatus(copy.getId(), BookCopyStatus.MAINTENANCE);
            }
        }

        flowLogService.logPayCompensation(compensation.getUserId(), compensation.getAmount(), compensationId);
    }

    public Compensation createCompensationForDamagedReturn(Long borrowRecordId) throws Exception {
        return createCompensation(borrowRecordId, "图书损坏归还", 50.0);
    }
}