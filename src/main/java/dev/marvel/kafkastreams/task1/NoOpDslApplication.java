package dev.marvel.kafkastreams.task1;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class NoOpDslApplication {

    private static final String INPUT_TOPIC_NAME = "task1-1";
    private static final String OUTPUT_TOPIC_NAME = "task1-2";

    public static void main(String[] args) {
        var streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev-marvel-dsl-noop-app");
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        var streamsBuilder = new StreamsBuilder();
        streamsBuilder.stream(INPUT_TOPIC_NAME).to(OUTPUT_TOPIC_NAME);
        var kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        kafkaStreams.start();
    }
}
