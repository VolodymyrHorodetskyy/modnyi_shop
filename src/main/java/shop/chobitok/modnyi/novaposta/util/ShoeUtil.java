package shop.chobitok.modnyi.novaposta.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.Status;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ShoeUtil {

    public static Status convertToStatus(Integer statusCode) {
        if (statusCode != null) {
            if (statusCode == 4 || statusCode == 41 || statusCode == 5 || statusCode == 6 || statusCode == 101) {
                return Status.ВІДПРАВЛЕНО;
            } else if (statusCode == 2) {
                return Status.ВИДАЛЕНО;
            } else if (statusCode == 3) {
                return Status.НЕ_ЗНАЙДЕНО;
            } else if (statusCode == 1) {
                return Status.СТВОРЕНО;
            } else if (statusCode == 7 || statusCode == 8) {
                return Status.ДОСТАВЛЕНО;
            } else if (statusCode == 102 || statusCode == 103 || statusCode == 108) {
                return Status.ВІДМОВА;
            } else if (statusCode == 9 || statusCode == 10 || statusCode == 11) {
                return Status.ОТРИМАНО;
            } else if (statusCode == 104) {
                return Status.ЗМІНА_АДРЕСУ;
            } else if (statusCode == 105) {
                return Status.ПРИПИНЕНО_ЗБЕРІГАННЯ;
            } else if (statusCode == 112) {
                return Status.ДАТА_ДОСТАВКИ_ПЕРЕНЕСЕНА_ОТРИМУВАЧЕМ;
            } else if (statusCode == 111) {
                return Status.НЕВДАЛА_СПРОБА_ДОСТАВКИ;
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

    public static List<String> readTXTFile(MultipartFile file) {
        List<String> stringList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (DateTimeParseException e) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(s, formatter);
            }
        }
        return null;
    }


}
