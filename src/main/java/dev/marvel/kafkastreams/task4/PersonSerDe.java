package dev.marvel.kafkastreams.task4;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class PersonSerDe implements Serde<Person> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Serializer<Person> serializer() {
        return (topic, data) -> {
            try {
                return mapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<Person> deserializer() {
        return (topic, data) ->  {
            try {
                return mapper.readValue(data, Person.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
