package shop.chobitok.modnyi.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import shop.chobitok.modnyi.entity.Status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileReader {

    private static String mailHtmlTemplate = "mail_template_tilda.html";
    private static String mailWhenReceivedHtmlTemplate = "mail_template_tilda_received.html";

    public static String getHtmlTemplate(Status status) {
        String htmlTemplate = mailHtmlTemplate;
        if (status == Status.ОТРИМАНО) {
            htmlTemplate = mailWhenReceivedHtmlTemplate;
        }
        Resource resource = new ClassPathResource(htmlTemplate);
        String data = null;
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
