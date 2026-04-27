package com.library.controller;

import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public Result<List<BorrowRecord>> getAllBorrows() {
        List<BorrowRecord> records = borrowService.findAll();
        return Result.success(records);
    }

    @GetMapping("/{id}")
    public Result<BorrowRecord> getBorrowById(@PathVariable Long id) {
        Optional<BorrowRecord> record = borrowService.findById(id);
        return record.map(Result::success).orElseGet(() -> Result.error("借阅记录不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecord>> getBorrowsByUserId(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowService.findByUserId(userId);
        return Result.success(records);
    }

    @GetMapping("/user/{userId}/active")
    public Result<List<BorrowRecord>> getActiveBorrowsByUserId(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowService.findActiveByUserId(userId);
        return Result.success(records);
    }

    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long bookId = request.get("bookId");
            
            if (userId == null || bookId == null) {
                return Result.error("userId 和 bookId 不能为空");
            }
            
            BorrowRecord record = borrowService.borrowBook(userId, bookId);
            return Result.success("借阅成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    public Result<BorrowRecord> returnBook(@PathVariable Long id, @RequestBody(required = false) Map<String, Boolean> request) {
        try {
            boolean isDamaged = request != null && Boolean.TRUE.equals(request.get("isDamaged"));
            BorrowRecord record = borrowService.returnBook(id, isDamaged);
            return Result.success("归还成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/renew")
    public Result<BorrowRecord> renewBook(@PathVariable Long id) {
        try {
            BorrowRecord record = borrowService.renewBook(id);
            return Result.success("续借成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> getOverdueRecords() {
        List<BorrowRecord> records = borrowService.findOverdueRecords();
        return Result.success(records);
    }

    @GetMapping("/user/{userId}/count")
    public Result<Integer> getCurrentBorrowCount(@PathVariable Long userId) {
        int count = borrowService.getCurrentBorrowCount(userId);
        return Result.success(count);
    }
}