package com.library.entity.enums;

public enum FlowType {
    BORROW("借阅"),
    RETURN("归还"),
    RENEW("续借"),
    RESERVE("预约"),
    CANCEL_RESERVE("取消预约"),
    AUTO_ASSIGN("预约自动分配"),
    FINE("罚金"),
    PAY_FINE("支付罚金"),
    COMPENSATION("赔偿"),
    PAY_COMPENSATION("支付赔偿"),
    BLACKLIST_ADD("加入黑名单"),
    BLACKLIST_REMOVE("移出黑名单");

    private String description;

    FlowType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}