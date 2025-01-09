package com.supergroup.core.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

public class DateUtil {

    public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.of("GMT"))
                                      .toInstant());
    }

    public static Long toTimestamp(LocalDateTime localDateTime) {
        return capped(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateFormat, formatter);
    }

    public static Long toTimestamp(String date, DateTimeFormatter format) {
        return toTime(LocalDate.parse(date, format));
    }

    public static Long toTime(LocalDate date) {
        LocalTime time = LocalTime.parse("00:00:00");
        ZoneOffset zone = ZoneOffset.of("Z");
        return date.toEpochSecond(time, zone);
    }

    public static Long countdownTime(LocalDateTime endDate) {
        var now = LocalDateTime.now();
        if (now.isAfter(endDate)) {
            return 0L;
        }
        return Duration.between(now, endDate).toMillis();
    }

    private static Instant capped(Instant instant) {
        Instant[] instants = { Instant.ofEpochMilli(Long.MIN_VALUE), instant, Instant.ofEpochMilli(Long.MAX_VALUE) };
        Arrays.sort(instants);
        return instants[1];
    }
}
