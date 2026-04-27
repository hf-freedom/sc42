package com.library.entity.enums;

public enum UserLevel {
    BRONZE("青铜会员", 3),
    SILVER("白银会员", 5),
    GOLD("黄金会员", 10),
    PLATINUM("白金会员", 15);

    private String description;
    private int maxBorrowCount;

    UserLevel(String description, int maxBorrowCount) {
        this.description = description;
        this.maxBorrowCount = maxBorrowCount;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxBorrowCount() {
        return maxBorrowCount;
    }
}