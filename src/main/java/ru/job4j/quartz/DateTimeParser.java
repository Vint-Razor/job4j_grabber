package ru.job4j.quartz;

import java.time.LocalDateTime;

public interface DateTimeParser {
    LocalDateTime parse(String parse);
}
