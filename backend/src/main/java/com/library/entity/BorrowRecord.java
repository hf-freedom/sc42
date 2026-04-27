package com.library.entity;

import com.library.entity.enums.BorrowStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BorrowRecord implements Serializable {
    private Long id;
    private Long userId;
    private Long bookId;
    private Long bookCopyId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private BorrowStatus status;
    private Integer renewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BorrowRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getBookCopyId() { return bookCopyId; }
    public void setBookCopyId(Long bookCopyId) { this.bookCopyId = bookCopyId; }
    public LocalDateTime getBorrowTime() { return borrowTime; }
    public void setBorrowTime(LocalDateTime borrowTime) { this.borrowTime = borrowTime; }
    public LocalDateTime getDueTime() { return dueTime; }
    public void setDueTime(LocalDateTime dueTime) { this.dueTime = dueTime; }
    public LocalDateTime getReturnTime() { return returnTime; }
    public void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }
    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }
    public Integer getRenewCount() { return renewCount; }
    public void setRenewCount(Integer renewCount) { this.renewCount = renewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}