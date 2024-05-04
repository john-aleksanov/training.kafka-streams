package dev.marvel.kafkastreams.task4;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

public class PersonSerDeApp {

    private static final Logger log = LoggerFactory.getLogger(PersonSerDeApp.class);
    private static final String INPUT_TOPIC_NAME = "task4";

    public static void main(String[] args) {
        var streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev-marvel-custom-serde-app");
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, PersonSerDe.class);

        var streamsBuilder = new StreamsBuilder();
        streamsBuilder.<String, Person>stream(INPUT_TOPIC_NAME)
            .filter((k, v) -> Objects.nonNull(v))
            .foreach((k, v) -> log.info("Person: {}", v));
        var kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        kafkaStreams.start();
    }
}
