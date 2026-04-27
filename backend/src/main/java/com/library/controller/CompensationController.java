package com.library.controller;

import com.library.common.Result;
import com.library.entity.Compensation;
import com.library.service.CompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/compensations")
@CrossOrigin(origins = "*")
public class CompensationController {

    @Autowired
    private CompensationService compensationService;

    @GetMapping
    public Result<List<Compensation>> getAllCompensations() {
        List<Compensation> compensations = compensationService.findAll();
        return Result.success(compensations);
    }

    @GetMapping("/{id}")
    public Result<Compensation> getCompensationById(@PathVariable Long id) {
        Optional<Compensation> compensation = compensationService.findById(id);
        return compensation.map(Result::success).orElseGet(() -> Result.error("赔偿记录不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Compensation>> getCompensationsByUserId(@PathVariable Long userId) {
        List<Compensation> compensations = compensationService.findByUserId(userId);
        return Result.success(compensations);
    }

    @GetMapping("/user/{userId}/pending")
    public Result<List<Compensation>> getPendingCompensationsByUserId(@PathVariable Long userId) {
        List<Compensation> compensations = compensationService.findPendingByUserId(userId);
        return Result.success(compensations);
    }

    @PostMapping
    public Result<Compensation> createCompensation(@RequestBody Map<String, Object> request) {
        try {
            Long borrowRecordId = request.get("borrowRecordId") != null ? 
                ((Number) request.get("borrowRecordId")).longValue() : null;
            String reason = (String) request.getOrDefault("reason", "图书损坏");
            Double amount = request.get("amount") != null ? 
                ((Number) request.get("amount")).doubleValue() : 50.0;
            
            if (borrowRecordId == null) {
                return Result.error("borrowRecordId 不能为空");
            }
            
            Compensation compensation = compensationService.createCompensation(borrowRecordId, reason, amount);
            return Result.success("创建成功", compensation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    public Result<Compensation> payCompensation(@PathVariable Long id) {
        try {
            compensationService.payCompensation(id);
            Optional<Compensation> compensation = compensationService.findById(id);
            return compensation.map(c -> Result.success("支付成功", c))
                    .orElseGet(() -> Result.error("赔偿记录不存在"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/has-pending")
    public Result<Boolean> hasPendingCompensation(@PathVariable Long userId) {
        boolean hasPending = compensationService.hasPendingCompensation(userId);
        return Result.success(hasPending);
    }
}