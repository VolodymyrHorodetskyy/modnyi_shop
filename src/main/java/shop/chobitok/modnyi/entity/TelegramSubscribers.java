package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class TelegramSubscribers extends Audit {
    private String chatId;
    private boolean available = true;

    public TelegramSubscribers() {
    }

    public TelegramSubscribers(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
