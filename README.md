# Bookstore Saga — Stories from Production "Build it" #1

A small online bookstore with **3 microservices** that use the **Saga pattern (choreography)** with **Spring Boot 3** and **Kafka**.

| Service | Port | Own database | Writes to topic | Listens to |
|---|---|---|---|---|
| order-service | 8081 | `orderdb` (H2) | `orders` | `inventory`, `payments` |
| inventory-service | 8082 | `inventorydb` (H2) | `inventory` | `orders`, `payments` |
| payment-service | 8083 | `paymentdb` (H2) | `payments` | `inventory` |

## The saga

```
ORDER_PLACED ──> inventory reserves stock ──> STOCK_RESERVED ──> payment charges card ──> PAYMENT_CHARGED ──> order CONFIRMED
                         │                                              │
                         └──> STOCK_FAILED ──> order CANCELLED          └──> PAYMENT_DECLINED ──> inventory RELEASES stock (compensation)
                                                                                                 └──> order CANCELLED
```

## Run it

You need Java 21, Maven and Docker.

```bash
docker compose up -d                 # start Kafka on localhost:9092
mvn clean install                    # build all modules

# in 3 terminals
mvn -pl order-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl payment-service spring-boot:run
```

Start **order-service first**: it creates the topics.

## Try the 3 stories

```bash
# 1. Happy path: 45.00 -> CONFIRMED
curl -s -X POST localhost:8081/orders -H 'Content-Type: application/json' -d '{"bookId":"ds-101","quantity":1}'

# 2. Out of stock -> CANCELLED (out of stock)
curl -s -X POST localhost:8081/orders -H 'Content-Type: application/json' -d '{"bookId":"micro-notes","quantity":1}'

# 3. 150.00 is over the card limit -> stock released (compensation) -> CANCELLED (card declined)
curl -s -X POST localhost:8081/orders -H 'Content-Type: application/json' -d '{"bookId":"cloud-deluxe","quantity":1}'

# Check an order (use the id from the response)
curl -s localhost:8081/orders/<id>
```

## What is missing (on purpose)

- **Transactional outbox** — saving to the DB and sending to Kafka are two actions. If the app crashes between them, the event is lost. Next "Build it" episode.
- **Retries and dead-letter topics** for broken messages.
- **Orchestration** with AWS Step Functions — another episode.
