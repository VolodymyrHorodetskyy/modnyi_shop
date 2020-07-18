package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.NotificationRepository;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String topic, String content, MessageType type, String ttn) {
        Notification notification = new Notification();
        notification.setMessageType(type);
        notification.setTopic(topic);
        notification.setContent(content);
        notification.setTtn(ttn);
        return notificationRepository.save(notification);
    }

    public Notification createNotification(String topic, String content, MessageType type) {
        return createNotification(topic, content, type, null);
    }

    public Page getNotifications(int page, int size, Boolean read) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("read1").and(Sort.by("createdDate").descending()));
        if (read != null) {
            return notificationRepository.findByRead1(read, pageable);
        }
        return notificationRepository.findAll(pageable);
    }

    public Notification makeRead(Long id) {
        Notification notification = notificationRepository.getOne(id);
        if (notification == null) {
            throw new ConflictException("Notification not found");
        }
        notification.setRead1(true);
        return notificationRepository.save(notification);
    }

    public Integer getUnreadAmount() {
        return notificationRepository.countByRead1(false);
    }

}
