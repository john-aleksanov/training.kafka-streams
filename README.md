# Project description

This project serves as a practical demonstration of Kafka Streams' capabilities, facilitating a hands-on approach to learning through a
series of well-defined tasks.

## Prerequisites

- Docker
- Docker Compose
- Java 17 (although this would also compile with JDK 16 as no sealed types were used in development)
- Gradle 7.3 or higher (see Gradle / Java compatibility matrix [here](https://docs.gradle.org/current/userguide/compatibility.html))

## Task 1: Kafka Streams Relay Application

### Objective

Develop a Kafka Streams application that seamlessly transfers messages from an input topic (task1-1) to an output topic (task1-2) without
altering the content.

### Implementation

The solution can be found under the `dev.marvel.kafkastreams.task1` package:

- NoOpDslApplication utilizes the Kafka Streams DSL for high-level stream processing.
- NoOpProcessorApiApplication employs the Processor API for more granular control over stream processing.

To run the application:

1. Run the [compose.yml](./compose.yml) file in the root directory to run a local Kafka cluster with three Kafka brokers:

```shell
docker compose up
```

2. Use these commands to set up the required topics within the Kafka cluster:

```shell
docker exec -it kafka3 kafka-topics.sh --create --bootstrap-server kafka3:19092 --topic task1-1 --partitions 3 --replication-factor 2
```

```shell
docker exec -it kafka3 kafka-topics.sh --create --bootstrap-server kafka3:19092 --topic task1-2 --partitions 3 --replication-factor 2
```

3. Run the application from your favorite IDE (either one).
4. Run a console Kafka producer for `task1-1`:

```shell
docker exec -it kafka3 kafka-console-producer.sh --bootstrap-server kafka3:19092 --topic task1-1
```

5. Run a console Kafka consumer from `task1-2`:

```shell
docker exec -it kafka3 kafka-console-consumer.sh --bootstrap-server kafka3:19092 --topic task1-2
```

6. Input messages via the producer and verify that identical messages are received by the consumer, demonstrating the effective relay
   functionality of the application.