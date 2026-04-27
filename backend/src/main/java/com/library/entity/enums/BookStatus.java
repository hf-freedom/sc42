package com.library.entity.enums;

public enum BookStatus {
    AVAILABLE("可借"),
    FULLY_BORROWED("已借完"),
    RESERVED_ONLY("仅预约"),
    MAINTENANCE("维护中");

    private String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}