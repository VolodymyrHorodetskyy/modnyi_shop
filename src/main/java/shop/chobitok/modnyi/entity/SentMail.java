package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class SentMail extends Audit {

    private Long clientId;
    private String mailTemplate;
    private String mail;

    public SentMail() {
    }

    public SentMail(Long clientId, String mailTemplate, String mail) {
        this.clientId = clientId;
        this.mailTemplate = mailTemplate;
        this.mail = mail;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

