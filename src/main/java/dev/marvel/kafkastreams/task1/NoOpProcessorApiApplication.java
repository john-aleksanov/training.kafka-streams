package dev.marvel.kafkastreams.task1;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class NoOpProcessorApiApplication {

    private static final String SOURCE_NAME = "source";
    private static final String SINK_NAME = "sink";
    private static final String INPUT_TOPIC_NAME = "task1-1";
    private static final String OUTPUT_TOPIC_NAME = "task1-2";

    public static void main(String[] args) {
        var streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev-marvel-processor-api-noop-app");
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        var topology = new Topology()
            .addSource(SOURCE_NAME, INPUT_TOPIC_NAME)
            .addSink(SINK_NAME, OUTPUT_TOPIC_NAME, SOURCE_NAME);
        var kafkaStreams = new KafkaStreams(topology, streamProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        kafkaStreams.start();
    }
}
