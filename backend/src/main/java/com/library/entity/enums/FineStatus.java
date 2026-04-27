package com.library.entity.enums;

public enum FineStatus {
    PENDING("待支付"),
    PAID("已支付"),
    WAIVED("已减免");

    private String description;

    FineStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}