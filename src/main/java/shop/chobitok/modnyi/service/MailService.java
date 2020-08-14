package shop.chobitok.modnyi.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.util.FileReader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String subject, String body, String... to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setSubject(subject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }

    public void sendStatusNotificationEmail(String to, Status status) {
        String htmlTemplate = FileReader.getHtmlTemplate();
        htmlTemplate = String.format(htmlTemplate, status);
        sendEmail("Статус вашого замовлення 'Модний чобіток'", htmlTemplate, to);
    }


}
