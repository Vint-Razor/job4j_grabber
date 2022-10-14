package ru.job4j.quartz;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void checkParse() {
        final HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String str = "2022-10-14T16:53:32+03:00";
        LocalDateTime expected = LocalDateTime.parse("2022-10-14T16:53:32");
        assertThat(parser.parse(str)).isEqualTo(expected);
    }
}