package com.library.entity;

import com.library.entity.enums.FineStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Fine implements Serializable {
    private Long id;
    private Long userId;
    private Long borrowRecordId;
    private Double amount;
    private Integer overdueDays;
    private FineStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Fine() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBorrowRecordId() { return borrowRecordId; }
    public void setBorrowRecordId(Long borrowRecordId) { this.borrowRecordId = borrowRecordId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }
    public FineStatus getStatus() { return status; }
    public void setStatus(FineStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}