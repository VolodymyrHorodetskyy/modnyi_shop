package shop.chobitok.modnyi.novaposta.util;

import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShoeUtil {

    public static Status convertToStatus(Integer statusCode) {
        if (statusCode != null) {
            if (statusCode == 4 || statusCode == 41 || statusCode == 5 || statusCode == 6 || statusCode == 101) {
                return Status.SENT;
            } else if (statusCode == 2) {
                return Status.DELETED;
            } else if (statusCode == 1) {
                return Status.CREATED;
            } else if (statusCode == 7 || statusCode == 8) {
                return Status.DELIVERED;
            } else if (statusCode == 102 || statusCode == 103 || statusCode == 108) {
                return Status.DENIED;
            } else if (statusCode == 9 || statusCode == 10 || statusCode == 11) {
                return Status.RECEIVED;
            }
        }
        return null;
    }

    public static List<String> readTXTFile(String path) {
        File file = new File(path);
        List<String> stringList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                stringList.add(st);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    public static LocalDateTime toLocalDateTime(String s) {
        if (!StringUtils.isEmpty(s)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(s, formatter);
        }
        return null;
    }


}
