package com.library.entity;

import com.library.entity.enums.ReservationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable {
    private Long id;
    private Long userId;
    private Long bookId;
    private Integer queuePosition;
    private ReservationStatus status;
    private LocalDateTime reservedTime;
    private LocalDateTime assignedTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reservation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public LocalDateTime getReservedTime() { return reservedTime; }
    public void setReservedTime(LocalDateTime reservedTime) { this.reservedTime = reservedTime; }
    public LocalDateTime getAssignedTime() { return assignedTime; }
    public void setAssignedTime(LocalDateTime assignedTime) { this.assignedTime = assignedTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}