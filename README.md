# Project description

This project serves as a practical demonstration of Kafka Streams' capabilities through a series of specific tasks.

## Prerequisites

- Docker
- Docker Compose
- Java 17 (although this would also compile with JDK 16 as no sealed types and other Java 17 features were used in development)
- Gradle 7.3 or higher (see Gradle / Java compatibility matrix [here](https://docs.gradle.org/current/userguide/compatibility.html))

## Task 1: Kafka Streams Relay Application

### Objective

Develop a Kafka Streams application that transfers messages from an input topic (task1-1) to an output topic (task1-2) without
altering the content.

### Implementation

The solution can be found under the `dev.marvel.kafkastreams.task1` package:

- `NoOpDslApplication` utilizes the Kafka Streams DSL for high-level stream processing.
- `NoOpProcessorApiApplication` employs the Processor API for more granular control over stream processing.

### How to run

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

## Task 2: Kafka Streams Processing Application

### Objective

Develop a Kafka Streams application that consumes text messages from a Kafka topic, performs various transformations including filtering
messages, transforming their content, branching a stream and merging the branches back. The stream / output should be covered by tests.

### Implementation

The solution can be found under the `dev.marvel.kafkastreams.task2` package:

- `StreamBuilderFactory` is a factory that produces an instance of `StreamsBuilder` for ease of testing.
- `StreamProcessingApplication` is the entrypoint that creates an instance of the factory, obtains a `StreamsBuilder` and starts Kafka
  Streams.

The stream does the following:

1. Pulls data from a `task2` topic.
2. Filters out messages with `null` values.
3. Splits each message value, which is presumed to be a sentence, into its constituent words. Each word is emitted as a new message where
   the key is
   the word's length and the value is the word itself.
4. Each word is logged to the console.
5. The stream divides into two paths:
    - Words shorter than ten characters are classified as 'short'.
    - Words ten characters or longer are classified as 'long'.
6. Messages in both branches are filtered such that only words with the letter 'a' are kept.
7. The short and long word streams are then merged back together.
8. Each word from the merged stream is logged to the console.

### How to run

1. Run the [compose.yml](./compose.yml) file in the root directory to run a local Kafka cluster with three Kafka brokers:

```shell
docker compose up
```

2. Set up a topic within the Kafka cluster:

```shell
docker exec -it kafka3 kafka-topics.sh --create --bootstrap-server kafka3:19092 --topic task2 --partitions 3 --replication-factor 2
```

3. Run the application from your favorite IDE.
4. Run a console Kafka producer for `task2`:

```shell
docker exec -it kafka3 kafka-console-producer.sh --bootstrap-server kafka3:19092 --topic task2
```

5. Input sentences via the producer and verify that the application logs individual words and words with the letter "a" on top of that.

### Tests

The stream is tested in the `StreamBuilderFactoryTest` class. For testing purposes, a custom `CountingAppender` is created and added
to the `dev.marvel.kafkastreams` Log4J logger. It counts the logging events for both logging occasions (steps 4 and 8 from the stream
pipeline above). Those values are then compared to the expected values.

## Task 3: Inner Join Application

### Objective

Develop a Kafka streams application that performs an inner join of messages from two topics over a specific time window.

### Implementation

See package `dev.marvel.kafkastreams.task3`.

1. There are two Kafka streams, call them "left" and "right" for convenience, each of which reads messages from its own kafka topic.
2. Each stream filters out `null`-value messages as well as messages whose value does not contain the ":" character as its expected
   format is "key:value".
3. Each message is then remapped to the key part of the "key:value" value becoming the key of the new message and the value part becoming
   the value.
4. Each resulting message is logged to the console.
5. The left and right streams are joined based on the message keys over a 60-second window with a 30-second out-of-band message grace
   period with the resulting value being in the format " {left stream value} : {right stream message value}".
6. Each resulting message is logged to the console.

### How to run

1. Run the [compose.yml](./compose.yml) file in the root directory to run a local Kafka cluster with three Kafka brokers:

```shell
docker compose up
```

2. Set up the required topics within the Kafka cluster:

```shell
docker exec -it kafka3 kafka-topics.sh --create --bootstrap-server kafka3:19092 --topic task3-1 --partitions 3 --replication-factor 2
```

```shell
docker exec -it kafka3 kafka-topics.sh --create --bootstrap-server kafka3:19092 --topic task3-2 --partitions 3 --replication-factor 2
```

3. Run the application from your favorite IDE.
4. Run a console Kafka producer for `task3-1`:

```shell
docker exec -it kafka3 kafka-console-producer.sh --bootstrap-server kafka3:19092 --topic task3-1
```

5. Run another console Kafka producer for `task3-2`:

```shell
docker exec -it kafka3 kafka-console-consumer.sh --bootstrap-server kafka3:19092 --topic task3-2
```

6. Input messages via the producers in the format "key:value" and verify that messages with the same key within any 60-second window indeed
   get joined and logged to the console.

