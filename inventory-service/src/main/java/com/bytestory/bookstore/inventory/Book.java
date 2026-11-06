package com.bytestory.bookstore.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Book {

    @Id
    private String id;
    private String title;
    private BigDecimal price;
    private int stock;

    protected Book() {
    }

    public Book(String id, String title, BigDecimal price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}
