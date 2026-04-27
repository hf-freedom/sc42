package com.library.entity.enums;

public enum BookCopyStatus {
    AVAILABLE("可借"),
    BORROWED("已借出"),
    DAMAGED("损坏"),
    LOST("丢失"),
    MAINTENANCE("维护中");

    private String description;

    BookCopyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}