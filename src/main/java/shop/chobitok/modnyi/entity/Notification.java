package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Notification extends Audit {

    @Column
    private String topic;
    @Column
    private String content;
    @Column
    private MessageType messageType;
    @Column
    private boolean read1 = false;
    @Column
    private String ttn;

    public Notification() {
    }

    public Notification(String topic, String content, MessageType messageType, String ttn) {
        this.topic = topic;
        this.content = content;
        this.messageType = messageType;
        this.ttn = ttn;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
