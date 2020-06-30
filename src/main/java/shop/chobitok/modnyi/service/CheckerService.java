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
    private MessageService messageService;

    public CheckerService(OrderService orderService, MessageService messageService) {
        this.orderService = orderService;
        this.messageService = messageService;
    }

    public List<Notification> checkCanceledOrders() {
        List<Notification> notifications = new ArrayList<>();
        List<Ordered> orderedList = orderService.getCanceled(true);
        for (Ordered ordered : orderedList) {
            notifications.add(messageService.createMessage("Відмова на пошті", MessageType.ORDER_CANCELED, ordered.getTtn()));
        }
        return notifications;
    }

}
