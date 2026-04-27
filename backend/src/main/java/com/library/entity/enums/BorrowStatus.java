package com.library.entity.enums;

public enum BorrowStatus {
    BORROWED("借阅中"),
    RETURNED("已归还"),
    RENEWED("已续借"),
    DAMAGED("损坏归还"),
    LOST("丢失");

    private String description;

    BorrowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}