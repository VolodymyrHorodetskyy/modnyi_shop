package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            notifications.add(notificationService.createNotification("Відмова на пошті", "", MessageType.ORDER_CANCELED, ordered.getTtn()));
        }
        return notifications;
    }

    public List<Notification> checkPayedKeepingOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Notification> notifications = new ArrayList<>();
        orderService.updateOrderStatusesNovaPosta();
        List<Ordered> arrivedOrders = orderService.getOrdersByStatus(Status.ДОСТАВЛЕНО);
        for (Ordered ordered : arrivedOrders) {
            LocalDateTime datePayedKeeping = ordered.getDatePayedKeepingNP();
            if (datePayedKeeping != null) {
                if (datePayedKeeping.plusDays(3).isAfter(LocalDateTime.now())) {
                    notifications.add(notificationService.createNotification("Платне зберігання з " + datePayedKeeping.format(formatter),
                            "Платне зберігання з " + datePayedKeeping.format(formatter), MessageType.PAYED_KEEPING, ordered.getTtn()));
                }
            }
        }
        return notifications;
    }

}
