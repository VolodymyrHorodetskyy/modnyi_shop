package shop.chobitok.modnyi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHelper {

    private final static String[] DATE_PATTERNS = {
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd"
    };

    public static LocalDate formDate(String date) throws DateTimeParseException {
        if (!checkDateStringFromFrontEnd(date)) {
            throw new DateTimeParseException("Date string does not meet front-end criteria", date, 0);
        }
        for (String pattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                // Attempt to parse to LocalDateTime first to avoid losing time information, then convert to LocalDate.
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException ignored) {
                // Ignore the exception and try the next pattern
            }
        }
        throw new DateTimeParseException("Failed to parse date string with known patterns", date, 0);
    }

    public static LocalDateTime formDateTime(String date) throws DateTimeParseException {
        if (!checkDateStringFromFrontEnd(date)) {
            throw new DateTimeParseException("Date string does not meet front-end criteria", date, 0);
        }
        for (String pattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(date, formatter);
            } catch (DateTimeParseException ignored) {
                // Ignore the exception and try the next pattern
            }
        }
        throw new DateTimeParseException("Failed to parse date string with known patterns", date, 0);
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
        if (localDateTime == null)
            return null;
        return localDateTime.withHour(0).withMinute(0).withSecond(0);
    }

    public static LocalDateTime makeDateEndOfDay(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return null;
        return localDateTime.withHour(23).withMinute(59).withSecond(59);
    }
}
