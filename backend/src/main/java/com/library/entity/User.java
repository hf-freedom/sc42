package com.library.entity;

import com.library.entity.enums.BlacklistStatus;
import com.library.entity.enums.UserLevel;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private Long id;
    private String username;
    private String realName;
    private UserLevel level;
    private Integer maxBorrowCount;
    private Double depositBalance;
    private BlacklistStatus blacklistStatus;
    private Integer overdueCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public UserLevel getLevel() { return level; }
    public void setLevel(UserLevel level) { this.level = level; }
    public Integer getMaxBorrowCount() { return maxBorrowCount; }
    public void setMaxBorrowCount(Integer maxBorrowCount) { this.maxBorrowCount = maxBorrowCount; }
    public Double getDepositBalance() { return depositBalance; }
    public void setDepositBalance(Double depositBalance) { this.depositBalance = depositBalance; }
    public BlacklistStatus getBlacklistStatus() { return blacklistStatus; }
    public void setBlacklistStatus(BlacklistStatus blacklistStatus) { this.blacklistStatus = blacklistStatus; }
    public Integer getOverdueCount() { return overdueCount; }
    public void setOverdueCount(Integer overdueCount) { this.overdueCount = overdueCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}