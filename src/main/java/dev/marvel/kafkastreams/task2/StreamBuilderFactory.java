package dev.marvel.kafkastreams.task2;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class StreamBuilderFactory {

    private static final Logger log = LoggerFactory.getLogger(StreamProcessingApplication.class);
    private static final String INPUT_TOPIC_NAME = "task2";

    public StreamsBuilder create() {
        var streamsBuilder = new StreamsBuilder();
        var branches = streamsBuilder.<String, String>stream(INPUT_TOPIC_NAME)
            .filter((k, v) -> Objects.nonNull(v))
            .flatMap((k, v) -> Arrays.stream(v.split(" "))
                .map(word -> new KeyValue<>(word.length(), word))
                .toList())
            .peek((k, v) -> log.info("Here's an intermediate result: key {}, value {}", k, v))
            .split(Named.as("words-"))
            .branch((k, v) -> k >= 10, Branched.as("long"))
            .defaultBranch(Branched.as("short"));
        branches.get("words-long")
            .filter((k, v) -> v.contains("a"))
            .merge(branches.get("words-short").filter((k, v) -> v.contains("a")))
            .foreach((k, v) -> log.info("Yay, I made it to the end of the stream: key {}, value {}", k, v));

        return streamsBuilder;
    }
}
