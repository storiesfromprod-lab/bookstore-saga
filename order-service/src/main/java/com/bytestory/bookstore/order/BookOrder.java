package com.bytestory.bookstore.order;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class BookOrder {

    @Id
    private String id;
    private String bookId;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String reason;

    protected BookOrder() {
    }

    public BookOrder(String id, String bookId, int quantity) {
        this.id = id;
        this.bookId = bookId;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public OrderStatus getStatus() { return status; }
    public String getReason() { return reason; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
}
