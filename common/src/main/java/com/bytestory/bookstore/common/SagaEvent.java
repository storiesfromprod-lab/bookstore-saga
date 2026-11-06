package com.bytestory.bookstore.common;

import java.math.BigDecimal;

/**
 * The one message that travels between services.
 * orderId is the saga's correlation ID: every event of one order carries it.
 */
public record SagaEvent(
        EventType type,
        String orderId,
        String bookId,
        int quantity,
        BigDecimal amount,
        String reason) {

    /** The next step of the same saga. */
    public SagaEvent next(EventType nextType, BigDecimal newAmount) {
        return new SagaEvent(nextType, orderId, bookId, quantity, newAmount, null);
    }

    /** A failed step of the same saga, with the reason. */
    public SagaEvent fail(EventType failType, String why) {
        return new SagaEvent(failType, orderId, bookId, quantity, amount, why);
    }
}
