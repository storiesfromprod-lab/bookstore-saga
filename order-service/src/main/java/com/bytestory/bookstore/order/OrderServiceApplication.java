package com.bytestory.bookstore.order;

import com.bytestory.bookstore.common.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    // Create the three topics when the order service starts.
    @Bean
    NewTopic ordersTopic() {
        return TopicBuilder.name(Topics.ORDERS).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic inventoryTopic() {
        return TopicBuilder.name(Topics.INVENTORY).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic paymentsTopic() {
        return TopicBuilder.name(Topics.PAYMENTS).partitions(1).replicas(1).build();
    }
}
