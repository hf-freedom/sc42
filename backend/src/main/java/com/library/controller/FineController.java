package com.library.controller;

import com.library.common.Result;
import com.library.entity.Fine;
import com.library.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin(origins = "*")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping
    public Result<List<Fine>> getAllFines() {
        List<Fine> fines = fineService.findAll();
        return Result.success(fines);
    }

    @GetMapping("/{id}")
    public Result<Fine> getFineById(@PathVariable Long id) {
        Optional<Fine> fine = fineService.findById(id);
        return fine.map(Result::success).orElseGet(() -> Result.error("罚金记录不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Fine>> getFinesByUserId(@PathVariable Long userId) {
        List<Fine> fines = fineService.findByUserId(userId);
        return Result.success(fines);
    }

    @GetMapping("/user/{userId}/pending")
    public Result<List<Fine>> getPendingFinesByUserId(@PathVariable Long userId) {
        List<Fine> fines = fineService.findPendingByUserId(userId);
        return Result.success(fines);
    }

    @PostMapping("/{id}/pay")
    public Result<Fine> payFine(@PathVariable Long id) {
        try {
            fineService.payFine(id);
            Optional<Fine> fine = fineService.findById(id);
            return fine.map(f -> Result.success("支付成功", f))
                    .orElseGet(() -> Result.error("罚金记录不存在"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/process-overdue")
    public Result<String> processOverdueRecords() {
        fineService.processOverdueRecords();
        return Result.success("处理完成", "已处理逾期记录并生成罚金");
    }

    @PostMapping("/check-blacklist")
    public Result<String> checkOverdueFinesForBlacklist() {
        fineService.checkOverdueFinesForBlacklist();
        return Result.success("处理完成", "已检查逾期罚金并更新黑名单");
    }

    @GetMapping("/user/{userId}/has-pending")
    public Result<Boolean> hasPendingFine(@PathVariable Long userId) {
        boolean hasPending = fineService.hasPendingFine(userId);
        return Result.success(hasPending);
    }
}