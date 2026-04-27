package com.library.service;

import com.library.entity.FlowLog;
import com.library.entity.enums.FlowType;
import com.library.repository.FlowLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlowLogService {

    @Autowired
    private FlowLogRepository flowLogRepository;

    public void log(FlowType flowType, Long userId, String description, Long relatedId, Double amount) {
        FlowLog flowLog = new FlowLog();
        flowLog.setFlowType(flowType);
        flowLog.setUserId(userId);
        flowLog.setDescription(description);
        flowLog.setRelatedId(relatedId);
        flowLog.setAmount(amount);
        flowLog.setCreatedAt(LocalDateTime.now());
        flowLogRepository.save(flowLog);
    }

    public void logBorrow(Long userId, String bookTitle, Long borrowRecordId) {
        log(FlowType.BORROW, userId, "借阅图书: " + bookTitle, borrowRecordId, null);
    }

    public void logReturn(Long userId, String bookTitle, Long borrowRecordId) {
        log(FlowType.RETURN, userId, "归还图书: " + bookTitle, borrowRecordId, null);
    }

    public void logRenew(Long userId, String bookTitle, Long borrowRecordId) {
        log(FlowType.RENEW, userId, "续借图书: " + bookTitle, borrowRecordId, null);
    }

    public void logReserve(Long userId, String bookTitle, Long reservationId) {
        log(FlowType.RESERVE, userId, "预约图书: " + bookTitle, reservationId, null);
    }

    public void logCancelReserve(Long userId, String bookTitle, Long reservationId) {
        log(FlowType.CANCEL_RESERVE, userId, "取消预约图书: " + bookTitle, reservationId, null);
    }

    public void logAutoAssign(Long userId, String bookTitle, Long borrowRecordId, Long reservationId) {
        log(FlowType.AUTO_ASSIGN, userId, "预约自动分配借阅: " + bookTitle + " (预约号:" + reservationId + ")", borrowRecordId, null);
    }

    public void logFine(Long userId, String bookTitle, Double amount, Long fineId) {
        log(FlowType.FINE, userId, "产生逾期罚金: " + bookTitle + ", 金额: " + amount, fineId, amount);
    }

    public void logPayFine(Long userId, Double amount, Long fineId) {
        log(FlowType.PAY_FINE, userId, "支付罚金, 金额: " + amount, fineId, amount);
    }

    public void logCompensation(Long userId, String bookTitle, Double amount, Long compensationId) {
        log(FlowType.COMPENSATION, userId, "产生赔偿: " + bookTitle + ", 金额: " + amount, compensationId, amount);
    }

    public void logPayCompensation(Long userId, Double amount, Long compensationId) {
        log(FlowType.PAY_COMPENSATION, userId, "支付赔偿, 金额: " + amount, compensationId, amount);
    }

    public void logBlacklistAdd(Long userId, String reason) {
        log(FlowType.BLACKLIST_ADD, userId, "加入黑名单: " + reason, null, null);
    }

    public void logBlacklistRemove(Long userId) {
        log(FlowType.BLACKLIST_REMOVE, userId, "移出黑名单", null, null);
    }

    public List<FlowLog> findAll() {
        return flowLogRepository.findAll();
    }

    public List<FlowLog> findByUserId(Long userId) {
        return flowLogRepository.findByUserId(userId);
    }
}