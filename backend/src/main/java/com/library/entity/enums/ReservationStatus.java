package com.library.entity.enums;

public enum ReservationStatus {
    PENDING("等待中"),
    ASSIGNED("已分配"),
    CANCELLED("已取消"),
    EXPIRED("已过期");

    private String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}