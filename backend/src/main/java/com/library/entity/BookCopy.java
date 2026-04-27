package com.library.entity;

import com.library.entity.enums.BookCopyStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BookCopy implements Serializable {
    private Long id;
    private Long bookId;
    private String barcode;
    private String libraryBranch;
    private BookCopyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookCopy() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getLibraryBranch() { return libraryBranch; }
    public void setLibraryBranch(String libraryBranch) { this.libraryBranch = libraryBranch; }
    public BookCopyStatus getStatus() { return status; }
    public void setStatus(BookCopyStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}