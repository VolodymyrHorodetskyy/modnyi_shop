package shop.chobitok.modnyi.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.util.FileReader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String subject, String body, String to) {
        sendEmail(subject, body, singletonList(to));
    }

    public void sendEmail(String subject, String body, List<String> toList) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setSubject(subject);
            for (String to : toList) {
                mimeMessageHelper.setTo(to);
                javaMailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendStatusNotificationEmail(String to, Status status) {
        String htmlTemplate = FileReader.getHtmlTemplate(status);
        htmlTemplate = htmlTemplate.replaceAll("%s", status.toString());
        sendEmail("Статус вашого замовлення 'Модний чобіток'", htmlTemplate, to);
    }


}
