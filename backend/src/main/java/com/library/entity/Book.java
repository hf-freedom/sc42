package com.library.entity;

import com.library.entity.enums.BookStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Book implements Serializable {
    private Long id;
    private String title;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer damagedCopies;
    private BookStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Book() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public Integer getDamagedCopies() { return damagedCopies; }
    public void setDamagedCopies(Integer damagedCopies) { this.damagedCopies = damagedCopies; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}