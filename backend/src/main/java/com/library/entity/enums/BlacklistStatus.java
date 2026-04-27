package com.library.entity.enums;

public enum BlacklistStatus {
    NORMAL("正常"),
    BLACKLISTED("黑名单"),
    RESTRICTED("限制借阅");

    private String description;

    BlacklistStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}