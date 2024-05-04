package dev.marvel.kafkastreams.task4;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PersonSerDeTest {

    private final PersonSerDe uut = new PersonSerDe();

    @Test
    void whenValidJsonThenDeserializedCorrectly() {

        // GIVEN
        var input = "{\"name\": \"Mike Ross\", \"company\": \"Pearson Specter\", \"position\": \"Associate\",\"experience\": 5}"
            .getBytes(StandardCharsets.UTF_8);
        var expected = new Person("Mike Ross", "Pearson Specter", "Associate", 5);

        // WHEN
        var result = uut.deserializer().deserialize("dummy", input);

        // THEN
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenValidDtoThenSerializedCorrectly() {
        // GIVEN
        var input = new Person("Mike Ross", "Pearson Specter", "Associate", 5);
        var expected = "{\"name\":\"Mike Ross\",\"company\":\"Pearson Specter\",\"position\":\"Associate\",\"experience\":5}"
            .getBytes(StandardCharsets.UTF_8);

        // WHEN
        var result = uut.serializer().serialize("dummy", input);

        // THEN
        assertThat(result).isEqualTo(expected);
    }
}