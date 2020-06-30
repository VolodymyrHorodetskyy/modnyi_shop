package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Notification extends Audit {

    @Column
    private String notif;
    @Column
    private MessageType messageType;
   @Column
    private boolean read1 = false;
    @Column
    private String ttn;

    public Notification() {
    }

    public Notification(String notif, MessageType messageType, String ttn) {
        this.notif = notif;
        this.messageType = messageType;
        this.ttn = ttn;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isRead1() {
        return read1;
    }

    public void setRead1(boolean read1) {
        this.read1 = read1;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }
}
