package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.StatusChangeRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formDateFromOrGetDefault;
import static shop.chobitok.modnyi.util.DateHelper.formDateToOrGetDefault;

@Service
public class CheckerService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final AppOrderRepository appOrderRepository;
    private final AppOrderService appOrderService;
    private final StatusChangeRepository statusChangeRepository;

    public CheckerService(OrderService orderService, OrderRepository orderRepository, NotificationService notificationService, AppOrderRepository appOrderRepository, AppOrderService appOrderService, StatusChangeRepository statusChangeRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.appOrderRepository = appOrderRepository;
        this.appOrderService = appOrderService;
        this.statusChangeRepository = statusChangeRepository;
    }

    public void checkPayedKeepingOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        orderService.updateOrdersByNovaPosta();
        List<Ordered> arrivedOrders = orderService.getOrdersByStatus(Status.ДОСТАВЛЕНО);
        for (Ordered ordered : arrivedOrders) {
            LocalDateTime datePayedKeeping = ordered.getDatePayedKeepingNP();
            if (datePayedKeeping != null) {
                if (LocalDateTime.now().plusDays(3).isAfter(datePayedKeeping)) {
                    notificationService.createNotification("Платне зберігання з " + datePayedKeeping.format(formatter),
                            ordered.getUser().getName(), MessageType.PAYED_KEEPING, ordered.getTtn());
                }
            }
        }
    }

    public void makeAppOrderNewAgain() {
        List<AppOrder> appOrders = appOrderRepository.findByStatusIn(Arrays.asList(AppOrderStatus.Чекаємо_оплату, AppOrderStatus.Не_Відповідає, AppOrderStatus.В_обробці));
        if (appOrders.size() > 0) {
            List<AppOrder> updated = new ArrayList<>();
            for (AppOrder appOrder : appOrders) {
                updated.add(appOrderService.changeStatus(appOrder, null, AppOrderStatus.Новий));
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

    public StringResponse checkMistakesInOrder(Long userId, String dateFrom, String dateTo) {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(formDateFromOrGetDefault(dateFrom));
        orderedSpecification.setTo(formDateToOrGetDefault(dateTo));
        if (userId != null) {
            orderedSpecification.setUserId(userId.toString());
        }
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        StringBuilder priceUnder500StringBuilder = new StringBuilder();
        StringBuilder nullOrderedShoesStringBuilder = new StringBuilder();
        StringBuilder commasNoEqualShoesSizeStringBuilder = new StringBuilder();
        priceUnder500StringBuilder.append("Ціна нижча за 500").append("\n");
        nullOrderedShoesStringBuilder.append("Взуття не вибрано").append("\n");
        commasNoEqualShoesSizeStringBuilder.append("Кількість крапок з комою не відповідає кількості взуття").append("\n");
        for (Ordered ordered : orderedList) {
            if (ordered.getPrice() < 500) {
                priceUnder500StringBuilder.append(ordered.getTtn()).append(" ").append(ordered.getUser().getName()).append("\n");
            }
            if (ordered.getOrderedShoeList() == null) {
                nullOrderedShoesStringBuilder.append(ordered.getTtn()).append(" ").append(ordered.getUser().getName()).append("\n");
            } else {
                int commas = 0;
                for (int i = 0; i < ordered.getPostComment().length(); i++) {
                    if (ordered.getPostComment().charAt(i) == ';') commas++;
                }
                if (commas != ordered.getOrderedShoeList().size() - 1) {
                    commasNoEqualShoesSizeStringBuilder.append(ordered.getTtn()).append(" ").append(ordered.getUser().getName()).append("\n");
                }
            }
        }
        return new StringResponse(priceUnder500StringBuilder.append("\n")
                .append("\n").append(nullOrderedShoesStringBuilder.toString())
                .append("\n").append(commasNoEqualShoesSizeStringBuilder.toString())
                .toString());
    }

    public StringResponse checkAppOrdersBecameOrders(Long userId, String dateFrom, String to) {
        AppOrderSpecification specification = new AppOrderSpecification();
        specification.setFromCreatedDate(formDateFromOrGetDefault(dateFrom));
        specification.setToCreatedDate(formDateToOrGetDefault(to));
        if (userId != null) {
            specification.setUserId(userId.toString());
        }
        List<AppOrder> appOrders = appOrderRepository.findAll(specification);
        StringBuilder response = new StringBuilder();
        response.append(appOrders.size())
                .append("\n")
                .append(appOrders.stream().filter(appOrder -> appOrder.getTtn() != null).count());
        return new StringResponse(response.toString());
    }
}

