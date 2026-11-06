package com.bytestory.bookstore.payment;

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
public class PaymentListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    // Fake card limit, so we can see a declined payment in the demo.
    private static final BigDecimal CARD_LIMIT = new BigDecimal("100.00");

    private final PaymentRepository payments;
    private final KafkaTemplate<String, SagaEvent> kafka;

    public PaymentListener(PaymentRepository payments, KafkaTemplate<String, SagaEvent> kafka) {
        this.payments = payments;
        this.kafka = kafka;
    }

    @KafkaListener(topics = Topics.INVENTORY)
    @Transactional
    public void on(SagaEvent event) {
        if (event.type() != EventType.STOCK_RESERVED) {
            return;
        }
        if (payments.existsById(event.orderId())) {
            return; // idempotent: never charge the same order twice
        }
        if (event.amount().compareTo(CARD_LIMIT) > 0) {
            payments.save(new Payment(event.orderId(), event.amount(), false));
            log.info("Order {}: payment of {} DECLINED", event.orderId(), event.amount());
            kafka.send(Topics.PAYMENTS, event.orderId(),
                    event.fail(EventType.PAYMENT_DECLINED, "card declined"));
            return;
        }
        payments.save(new Payment(event.orderId(), event.amount(), true));
        log.info("Order {}: charged {}", event.orderId(), event.amount());
        kafka.send(Topics.PAYMENTS, event.orderId(), event.next(EventType.PAYMENT_CHARGED, event.amount()));
    }
}
