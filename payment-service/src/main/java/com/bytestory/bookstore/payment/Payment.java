package com.bytestory.bookstore.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/** One payment per order: orderId is the idempotency key. */
@Entity
public class Payment {

    @Id
    private String orderId;
    private BigDecimal amount;
    private boolean charged;

    protected Payment() {
    }

    public Payment(String orderId, BigDecimal amount, boolean charged) {
        this.orderId = orderId;
        this.amount = amount;
        this.charged = charged;
    }

    public String getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public boolean isCharged() { return charged; }
}
