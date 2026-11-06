package com.bytestory.bookstore.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** One reservation per order. The orderId is also our idempotency key. */
@Entity
public class Reservation {

    @Id
    private String orderId;
    private String bookId;
    private int quantity;
    private boolean released;

    protected Reservation() {
    }

    public Reservation(String orderId, String bookId, int quantity) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public String getOrderId() { return orderId; }
    public String getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public boolean isReleased() { return released; }

    public void setReleased(boolean released) { this.released = released; }
}
