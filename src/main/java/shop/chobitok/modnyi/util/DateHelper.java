package shop.chobitok.modnyi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {


    public static LocalDate formDate(String date) {
        if (!checkDateStringFromFrontEnd(date)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime formDateTime(String date) {
        if (!checkDateStringFromFrontEnd(date)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }

    public static LocalDateTime formDateTimeFromOrGetDefault(String dateTimeFrom) {
        LocalDateTime localDateTime = formDateTime(dateTimeFrom);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now().minusDays(7);
        }
        return localDateTime.with(LocalTime.of(0, 0));
    }


    public static LocalDateTime formDateTimeToOrGetDefault(String dateTimeTo) {
        LocalDateTime localDateTime = formDateTime(dateTimeTo);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }
        localDateTime = localDateTime.with(LocalTime.of(23, 59));
        return localDateTime;
    }

    public static boolean checkDateStringFromFrontEnd(String s) {
        if (s == null || s.contains("null") || s.isBlank()) {
            return false;
        }
        return true;
    }

    public static LocalDateTime formLocalDateTimeStartOfTheDay(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.of(0, 0, 0));
    }

    public static LocalDateTime makeDateBeginningOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0);
    }

    public static LocalDateTime makeDateEndOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59);
    }
}
