package com.bytestory.bookstore.inventory;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    // Demo books: one cheap, one expensive (payment will decline it), one out of stock.
    @Bean
    CommandLineRunner seedBooks(BookRepository books) {
        return args -> {
            books.save(new Book("ds-101", "Distributed Systems 101", new BigDecimal("45.00"), 5));
            books.save(new Book("cloud-deluxe", "Cloud Patterns Deluxe", new BigDecimal("150.00"), 2));
            books.save(new Book("micro-notes", "Microservices Notebook", new BigDecimal("30.00"), 0));
        };
    }
}
