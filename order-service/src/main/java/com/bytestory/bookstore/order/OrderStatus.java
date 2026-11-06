package com.bytestory.bookstore.order;

public enum OrderStatus {
    PENDING,    // saga is running (semantic lock)
    CONFIRMED,  // saga finished: happy path
    CANCELLED   // saga finished: compensated
}
