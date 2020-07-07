package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.entity.Ordered;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckerService {

    private OrderService orderService;
    private NotificationService notificationService;

    public CheckerService(OrderService orderService, NotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    public List<Notification> checkCanceledOrders() {
        List<Notification> notifications = new ArrayList<>();
        List<Ordered> orderedList = orderService.getCanceled(true);
        for (Ordered ordered : orderedList) {
            notifications.add(notificationService.createMessage("Відмова на пошті", MessageType.ORDER_CANCELED, ordered.getTtn()));
        }
        return notifications;
    }

}
