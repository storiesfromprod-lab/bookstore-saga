package com.bytestory.bookstore.common;

/** One topic per service. A service only WRITES to its own topic. */
public final class Topics {
    public static final String ORDERS = "orders";
    public static final String INVENTORY = "inventory";
    public static final String PAYMENTS = "payments";

    private Topics() {
    }
}
