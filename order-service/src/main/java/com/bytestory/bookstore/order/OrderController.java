package com.bytestory.bookstore.order;

import com.bytestory.bookstore.common.EventType;
import com.bytestory.bookstore.common.SagaEvent;
import com.bytestory.bookstore.common.Topics;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    record NewOrder(String bookId, int quantity) {
    }

    private final OrderRepository orders;
    private final KafkaTemplate<String, SagaEvent> kafka;

    public OrderController(OrderRepository orders, KafkaTemplate<String, SagaEvent> kafka) {
        this.orders = orders;
        this.kafka = kafka;
    }

    @PostMapping
    @Transactional
    public BookOrder placeOrder(@RequestBody NewOrder request) {
        // Step 1 of the saga: a LOCAL transaction in the order database.
        String orderId = UUID.randomUUID().toString();
        BookOrder order = orders.save(new BookOrder(orderId, request.bookId(), request.quantity()));

        // Tell the world. Inventory is listening.
        // (Saving + publishing are two actions: the outbox pattern fixes that. Next episode!)
        SagaEvent placed = new SagaEvent(EventType.ORDER_PLACED, orderId,
                request.bookId(), request.quantity(), null, null);
        kafka.send(Topics.ORDERS, orderId, placed);
        return order;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookOrder> getOrder(@PathVariable String id) {
        return ResponseEntity.of(orders.findById(id));
    }
}
