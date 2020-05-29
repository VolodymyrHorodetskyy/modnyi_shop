package shop.chobitok.modnyi.util;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static LocalDateTime formDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }

    public static LocalDateTime formDateFrom(String dateTimeFrom) {
        LocalDateTime localDateTime = formDate(dateTimeFrom);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now().minusDays(7);
        }
        return localDateTime.with(LocalTime.of(0, 0));
    }

    public static LocalDateTime formDateTo(String dateTimeTo) {
        LocalDateTime localDateTime = formDate(dateTimeTo);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }
        localDateTime = localDateTime.with(LocalTime.of(23, 59));
        return localDateTime;
    }

}
