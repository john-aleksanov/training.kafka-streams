package dev.marvel.kafkastreams.task2;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class StreamBuilderFactoryTest {

    private final StreamBuilderFactory uut = new StreamBuilderFactory();
    private final CountingAppender appender = ((LoggerContext) LogManager.getContext(false)).getConfiguration().getAppender("TestAppender");

    @Test
    void test() throws Exception {
        // GIVEN
        var streamProperties = new Properties();
        streamProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        var testDriver = new TopologyTestDriver(uut.create().build(), streamProperties);
        var stringSerializer = new StringSerializer();
        var inputTopic = testDriver.createInputTopic("task2", stringSerializer, stringSerializer);

        // WHEN
        var inputString = "Hey there, this is some long sentence with one more-than-ten-character word";
        inputTopic.pipeInput(inputString);

        // THEN
        assertThat(appender.getTotalWordCount()).isEqualTo(inputString.split(" ").length);
        assertThat(appender.getWordWithAWordCount()).isEqualTo(1);
    }
}