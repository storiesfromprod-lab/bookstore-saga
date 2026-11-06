package com.bytestory.bookstore.common;

/** Every step of the saga, as an event. */
public enum EventType {
    ORDER_PLACED,      // order-service
    STOCK_RESERVED,    // inventory-service
    STOCK_FAILED,      // inventory-service
    PAYMENT_CHARGED,   // payment-service
    PAYMENT_DECLINED   // payment-service
}
