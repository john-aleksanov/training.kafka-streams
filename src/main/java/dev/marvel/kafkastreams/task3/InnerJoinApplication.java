package dev.marvel.kafkastreams.task3;

import dev.marvel.kafkastreams.task2.StreamProcessingApplication;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

public class InnerJoinApplication {

    private static final Logger log = LoggerFactory.getLogger(StreamProcessingApplication.class);
    private static final String LEFT_TOPIC_NAME = "task3-1";
    private static final String RIGHT_TOPIC_NAME = "task3-2";

    public static void main(String[] args) {
        var streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev-marvel-inner-join-app");
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        Predicate<String, String> filterPredicate = (k, v) -> Objects.nonNull(v) && v.contains(":");
        KeyValueMapper<String, String, KeyValue<String, String>> keyValueMapper = (k, v) -> {
            var split = v.split(":");
            return new KeyValue<>(split[0], split[1]);
        };
        ForeachAction<String, String> logger = (k, v) -> log.info("Here's an intermediate result: key {}, value {}", k, v);

        var streamBuilder = new StreamsBuilder();
        var leftStream = streamBuilder.<String, String>stream(LEFT_TOPIC_NAME)
            .filter(filterPredicate)
            .map(keyValueMapper)
            .peek(logger);

        streamBuilder.<String, String>stream(RIGHT_TOPIC_NAME)
            .filter(filterPredicate)
            .map(keyValueMapper)
            .peek(logger)
            .join(leftStream, (right, left) -> String.format("%s : %s", left, right), JoinWindows.ofTimeDifferenceAndGrace(
                Duration.ofSeconds(60), Duration.ofSeconds(30)))
            .foreach((k, v) -> log.info("Yay, I made it to the end of the stream: key {}, value {}", k, v));

        var kafkaStreams = new KafkaStreams(streamBuilder.build(), streamProperties);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        kafkaStreams.start();
    }
}
