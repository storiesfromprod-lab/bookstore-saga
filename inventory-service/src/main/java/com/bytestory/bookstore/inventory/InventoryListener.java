package com.bytestory.bookstore.inventory;

import com.bytestory.bookstore.common.EventType;
import com.bytestory.bookstore.common.SagaEvent;
import com.bytestory.bookstore.common.Topics;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);

    private final BookRepository books;
    private final ReservationRepository reservations;
    private final KafkaTemplate<String, SagaEvent> kafka;

    public InventoryListener(BookRepository books, ReservationRepository reservations,
                             KafkaTemplate<String, SagaEvent> kafka) {
        this.books = books;
        this.reservations = reservations;
        this.kafka = kafka;
    }

    @KafkaListener(topics = {Topics.ORDERS, Topics.PAYMENTS})
    @Transactional
    public void on(SagaEvent event) {
        switch (event.type()) {
            case ORDER_PLACED -> reserve(event);
            case PAYMENT_DECLINED -> release(event);   // compensation
            default -> { }
        }
    }

    private void reserve(SagaEvent event) {
        if (reservations.existsById(event.orderId())) {
            return; // idempotent: we already handled this order
        }
        Book book = books.findById(event.bookId()).orElse(null);
        if (book == null || book.getStock() < event.quantity()) {
            log.info("Order {}: out of stock", event.orderId());
            kafka.send(Topics.INVENTORY, event.orderId(),
                    event.fail(EventType.STOCK_FAILED, "out of stock"));
            return;
        }
        book.setStock(book.getStock() - event.quantity());
        reservations.save(new Reservation(event.orderId(), book.getId(), event.quantity()));

        BigDecimal amount = book.getPrice().multiply(BigDecimal.valueOf(event.quantity()));
        log.info("Order {}: reserved {} x {} (stock left: {})",
                event.orderId(), event.quantity(), book.getId(), book.getStock());
        kafka.send(Topics.INVENTORY, event.orderId(), event.next(EventType.STOCK_RESERVED, amount));
    }

    // COMPENSATING TRANSACTION: undo the reservation.
    private void release(SagaEvent event) {
        reservations.findById(event.orderId())
                .filter(reservation -> !reservation.isReleased())   // idempotent
                .ifPresent(reservation -> {
                    Book book = books.findById(reservation.getBookId()).orElseThrow();
                    book.setStock(book.getStock() + reservation.getQuantity());
                    reservation.setReleased(true);
                    log.info("Order {}: COMPENSATION -> released {} x {} (stock back to {})",
                            event.orderId(), reservation.getQuantity(), book.getId(), book.getStock());
                });
    }
}
