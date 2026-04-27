package com.library.entity;

import com.library.entity.enums.FlowType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FlowLog implements Serializable {
    private Long id;
    private Long userId;
    private FlowType flowType;
    private String description;
    private Long relatedId;
    private Double amount;
    private LocalDateTime createdAt;

    public FlowLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public FlowType getFlowType() { return flowType; }
    public void setFlowType(FlowType flowType) { this.flowType = flowType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}