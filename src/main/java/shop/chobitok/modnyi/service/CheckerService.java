package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.StatusChangeRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CheckerService {

    private OrderService orderService;
    private NotificationService notificationService;
    private AppOrderRepository appOrderRepository;
    private AppOrderService appOrderService;
    private StatusChangeRepository statusChangeRepository;

    public CheckerService(OrderService orderService, NotificationService notificationService, AppOrderRepository appOrderRepository, AppOrderService appOrderService, StatusChangeRepository statusChangeRepository) {
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.appOrderRepository = appOrderRepository;
        this.appOrderService = appOrderService;
        this.statusChangeRepository = statusChangeRepository;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        List<Notification> notifications = new ArrayList<>();
        orderService.updateOrdersByNovaPosta();
        List<Ordered> arrivedOrders = orderService.getOrdersByStatus(Status.ДОСТАВЛЕНО);
        for (Ordered ordered : arrivedOrders) {
            LocalDateTime datePayedKeeping = ordered.getDatePayedKeepingNP();
            if (datePayedKeeping != null) {
                if (LocalDateTime.now().plusDays(3).isAfter(datePayedKeeping)) {
                    notifications.add(notificationService.createNotification("Платне зберігання з " + datePayedKeeping.format(formatter),
                            ordered.getUser().getName(), MessageType.PAYED_KEEPING, ordered.getTtn()));
                }
            }
        }
        return notifications;
    }

    public void makeAppOrderNewAgain() {
        List<AppOrder> appOrders = appOrderRepository.findByStatusIn(Arrays.asList(AppOrderStatus.Чекаємо_оплату, AppOrderStatus.Не_Відповідає, AppOrderStatus.В_обробці));
        if (appOrders.size() > 0) {
            List<AppOrder> updated = new ArrayList<>();
            StringBuilder phones = new StringBuilder();
            for (AppOrder appOrder : appOrders) {
                updated.add(appOrderService.changeStatus(appOrder, null, AppOrderStatus.Новий));
                phones.append(appOrder.getPhone() + ", ");
            }

            appOrderRepository.saveAll(updated);
        }
    }

    public void checkSendOrdersAndTakeMoreFiveDays() {
        List<StatusChangeRecord> statusChangeRecords = statusChangeRepository.findAllByCreatedDateGreaterThanEqualAndNewStatus(LocalDateTime.now().minusDays(30), Status.ВІДПРАВЛЕНО);
        for (StatusChangeRecord statusChangeRecord : statusChangeRecords) {
            List<StatusChangeRecord> statusChangeRecordList = statusChangeRepository.findOneByNewStatusInAndOrderedId(Arrays.asList(Status.ДОСТАВЛЕНО, Status.ОТРИМАНО, Status.ВІДМОВА),
                    statusChangeRecord.getOrdered().getId());
            if ((statusChangeRecordList == null || statusChangeRecordList.size() == 0)
                    && Duration.between(statusChangeRecord.getCreatedDate(), LocalDateTime.now()).toDays() > 4) {
                notificationService.createNotification("Статус відправлено більше ніж 5 днів",
                        statusChangeRecord.getOrdered().getTtn(), MessageType.NEED_ATTENTION);
            }
        }
    }

}
