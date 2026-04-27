package com.library.entity.enums;

public enum CompensationStatus {
    PENDING("待赔偿"),
    PAID("已赔偿"),
    WAIVED("已减免");

    private String description;

    CompensationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}