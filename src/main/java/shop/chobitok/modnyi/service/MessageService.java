package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Notification createMessage(String message, MessageType type, String ttn) {
        List<Notification> notifications = messageRepository.findByTtnAndMessageType(ttn, MessageType.ORDER_CANCELED);
        if (notifications.size() > 0) {
            for (Notification notification1 : notifications) {
                if (notification1.getCreatedDate().plusDays(2).isBefore(LocalDateTime.now())) {
                    return messageRepository.save(new Notification(message, type, ttn));
                }
            }
        } else {
            return messageRepository.save(new Notification(message, type, ttn));
        }
        return null;
    }

    public Notification createMessage(String message, MessageType type) {
        return createMessage(message, type);
    }


}
