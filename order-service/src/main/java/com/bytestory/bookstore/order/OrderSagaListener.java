package com.bytestory.bookstore.order;

import com.bytestory.bookstore.common.SagaEvent;
import com.bytestory.bookstore.common.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** The order service listens to the END of the saga: success or failure. */
@Component
public class OrderSagaListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaListener.class);
    private final OrderRepository orders;

    public OrderSagaListener(OrderRepository orders) {
        this.orders = orders;
    }

    @KafkaListener(topics = {Topics.INVENTORY, Topics.PAYMENTS})
    @Transactional
    public void on(SagaEvent event) {
        switch (event.type()) {
            case PAYMENT_CHARGED -> finish(event, OrderStatus.CONFIRMED);
            case STOCK_FAILED, PAYMENT_DECLINED -> finish(event, OrderStatus.CANCELLED);
            default -> { } // not for us
        }
    }

    private void finish(SagaEvent event, OrderStatus newStatus) {
        orders.findById(event.orderId()).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.PENDING) {
                return; // idempotent: this saga already finished
            }
            order.setStatus(newStatus);
            order.setReason(event.reason());
            log.info("Order {} -> {} {}", event.orderId(), newStatus,
                    event.reason() == null ? "" : "(" + event.reason() + ")");
        });
    }
}
