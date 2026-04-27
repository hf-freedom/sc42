package com.library.entity;

import com.library.entity.enums.CompensationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Compensation implements Serializable {
    private Long id;
    private Long userId;
    private Long borrowRecordId;
    private Long bookCopyId;
    private Double amount;
    private String reason;
    private CompensationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidDate;

    public Compensation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBorrowRecordId() { return borrowRecordId; }
    public void setBorrowRecordId(Long borrowRecordId) { this.borrowRecordId = borrowRecordId; }
    public Long getBookCopyId() { return bookCopyId; }
    public void setBookCopyId(Long bookCopyId) { this.bookCopyId = bookCopyId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public CompensationStatus getStatus() { return status; }
    public void setStatus(CompensationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDateTime paidDate) { this.paidDate = paidDate; }
}