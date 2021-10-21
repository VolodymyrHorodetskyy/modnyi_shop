package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.StatusChangeRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final NPOrderMapper npOrderMapper;
    private final StatisticService statisticService;

    public CheckerService(OrderService orderService, OrderRepository orderRepository, NotificationService notificationService, AppOrderRepository appOrderRepository, AppOrderService appOrderService, StatusChangeRepository statusChangeRepository, NPOrderMapper npOrderMapper, StatisticService statisticService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.appOrderRepository = appOrderRepository;
        this.appOrderService = appOrderService;
        this.statusChangeRepository = statusChangeRepository;
        this.npOrderMapper = npOrderMapper;
        this.statisticService = statisticService;
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
        makeAppOrdersNew(appOrderRepository.findByStatusIn(Arrays.asList(AppOrderStatus.Чекаємо_оплату, AppOrderStatus.Не_Відповідає, AppOrderStatus.В_обробці)));
    }

    public void checkRemindOnAppOrdersAndMakeThemNewAgain() {
        makeAppOrdersNew(appOrderRepository.findByRemindOnIsLessThanEqual(LocalDateTime.now()));
    }

    private void makeAppOrdersNew(List<AppOrder> appOrders) {
        if (appOrders.size() > 0) {
            List<AppOrder> updated = new ArrayList<>();
            for (AppOrder appOrder : appOrders) {
                updated.add(appOrderService.changeStatus(appOrder, null, AppOrderStatus.Новий, appOrder.getRemindOn() == null));
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

    public StringResponse checkMistakesInOrder(Long userId, String from, String to) {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        LocalDateTime fromDate = formDateFromOrGetDefault(from);
        LocalDateTime toDate = formDateToOrGetDefault(to);
        orderedSpecification.setFrom(fromDate.minusDays(7));
        orderedSpecification.setTo(toDate);
        orderedSpecification.setStatusNotIn(Collections.singletonList(Status.ВИДАЛЕНО));
        StringBuilder response = new StringBuilder();
        response.append(fromDate).append(" - ")
                .append(to == null ? "зараз" : to).append("\n\n");
        if (userId != null) {
            orderedSpecification.setUserId(userId.toString());
            response.append("Id менеджера : ").append(userId).append("\n");
        }
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        StringBuilder priceUnder500StringBuilder = null;
        StringBuilder nullOrderedShoesStringBuilder = null;
        StringBuilder commasNoEqualShoesSizeStringBuilder = null;
        StringBuilder discountIsNull = null;
        StringBuilder priceIsNotCorrect = null;
        for (Ordered ordered : orderedList) {
            String ttn = ordered.getTtn();
            String userName = ordered.getUser().getName();
            if (ordered.getOrderedShoeList() == null || ordered.getOrderedShoeList().size() == 0) {
                if (nullOrderedShoesStringBuilder == null) {
                    nullOrderedShoesStringBuilder = new StringBuilder();
                    nullOrderedShoesStringBuilder.append("Взуття не вибрано").append("\n");

                }
                nullOrderedShoesStringBuilder.append(ttn).append(" ").append(userName).append("\n");
            } else if (ordered.getPrice() < 500) {
                if (priceUnder500StringBuilder == null) {
                    priceUnder500StringBuilder = new StringBuilder();
                    priceUnder500StringBuilder.append("Ціна нижча за 500").append("\n");
                }
                priceUnder500StringBuilder.append(ttn).append(" ").append(userName).append("\n");
            } else {
                int commas = 0;
                for (int i = 0; i < ordered.getPostComment().length(); i++) {
                    if (ordered.getPostComment().charAt(i) == ';') commas++;
                }
                if (commas != ordered.getOrderedShoeList().size() - 1) {
                    if (commasNoEqualShoesSizeStringBuilder == null) {
                        commasNoEqualShoesSizeStringBuilder = new StringBuilder();
                        commasNoEqualShoesSizeStringBuilder.append("Кількість крапок з комою не відповідає кількості взуття").append("\n");
                    }
                    commasNoEqualShoesSizeStringBuilder.append(ttn).append(" ").append(userName).append("\n");
                }
            }
            if (ordered.getOrderedShoeList().size() > 1) {
                if (checkDiscountIsNull(ordered)) {
                    if (discountIsNull == null) {
                        discountIsNull = new StringBuilder();
                        discountIsNull.append("В замовленні більше двох пар, але немає знижки").append("\n");
                    }
                    discountIsNull.append(ttn).append(" ").append(userName)
                            .append("\n");
                } else if (checkPriceIsNotCorrect(ordered)) {
                    if (priceIsNotCorrect == null) {
                        priceIsNotCorrect = new StringBuilder();
                        priceIsNotCorrect.append("Неправильна ціна в замовленні").append("\n");
                    }
                    priceIsNotCorrect.append("Ціна зараз: ").append(ordered.getPrice())
                            .append(" ,ціна яка повинна бути: ")
                            .append(npOrderMapper.countDiscount(ordered.getOrderedShoeList(), ordered.getDiscount()))
                            .append("\n");
                    priceIsNotCorrect.append(ttn).append(" ").append(userName)
                            .append("\n\n");
                }
            }
        }
        if (priceUnder500StringBuilder != null) {
            response.append(priceUnder500StringBuilder).append("\n");
        }
        if (nullOrderedShoesStringBuilder != null) {
            response.append(nullOrderedShoesStringBuilder).append("\n");
        }
        if (commasNoEqualShoesSizeStringBuilder != null) {
            response.append(commasNoEqualShoesSizeStringBuilder).append("\n");
        }
        if (discountIsNull != null) {
            response.append(discountIsNull.toString()).append("\n");
        }
        if (priceIsNotCorrect != null) {
            response.append(priceIsNotCorrect.toString()).append("\n");
        }
        return new StringResponse(response.toString());
    }

    private boolean checkDiscountIsNull(Ordered ordered) {
        boolean result = false;
        if (ordered.getDiscount() == null) {
            result = true;
        }
        return result;
    }

    private boolean checkPriceIsNotCorrect(Ordered ordered) {
        boolean result = false;
        if (Math.abs(npOrderMapper.countDiscount(ordered.getOrderedShoeList(), ordered.getDiscount()) - ordered.getPrice()) > 199) {
            result = true;
        }
        return result;
    }

    public StringResponse checkAppOrdersBecameOrders(Long userId, String from, String to) {
        AppOrderSpecification specification = new AppOrderSpecification();
        LocalDateTime fromDate = formDateFromOrGetDefault(from);
        LocalDateTime toDate = formDateToOrGetDefault(to);
        specification.setFromCreatedDate(fromDate);
        specification.setToCreatedDate(toDate);
        specification.setUserId(userId.toString());
        String ordersByUserToPay;
        StringBuilder response = new StringBuilder();
        response.append(fromDate).append(" - ")
                .append(to == null ? "зараз" : to).append("\n");
        ordersByUserToPay = statisticService.getAllOrdersByUser(fromDate, userId);
        specification.setUserId(userId.toString());
        response.append("Id менеджера : ").append(userId).append("\n")
                .append(ordersByUserToPay).append("\n\n");

        List<AppOrder> appOrders = appOrderRepository.findAll(specification);
        long amountWithTtn = appOrders.stream().filter(appOrder -> appOrder.getTtn() != null).count();
        Map<AppOrderCancellationReason, Integer> appOrderCancellationReasonIntegerMap = new HashMap<>();
        StringBuilder reasonCommentsForOther = new StringBuilder();
        for (AppOrder appOrder : appOrders) {
            if (appOrder.getStatus() == AppOrderStatus.Скасовано) {
                if (appOrder.getCancellationReason() == AppOrderCancellationReason.ІНШЕ) {
                    reasonCommentsForOther.append("id : ").append(appOrder.getId()).append(" комент : ")
                            .append(appOrder.getComment()).append("\n");
                } else {
                    Integer amount = appOrderCancellationReasonIntegerMap.get(appOrder.getCancellationReason());
                    if (amount == null) {
                        appOrderCancellationReasonIntegerMap.put(appOrder.getCancellationReason(), 1);
                    } else {
                        appOrderCancellationReasonIntegerMap.put(appOrder.getCancellationReason(), ++amount);
                    }
                }
            }
        }
        StringBuilder cancellationReasonStats = new StringBuilder();
        for (Map.Entry entry : appOrderCancellationReasonIntegerMap.entrySet()) {
            cancellationReasonStats.append(entry.getKey()).append(" : ")
                    .append(entry.getValue()).append("\n");
        }
        response.append("Загальна кількість заявок : ").append(appOrders.size())
                .append("\n").append("Кількість заявок з ттн : ")
                .append(amountWithTtn)
                .append("\n").append("% : ")
                .append(appOrders.size() != 0 ? amountWithTtn * 100 / appOrders.size() : 0)
                .append("\n").append("Статистика причин скасування заявок : ")
                .append(cancellationReasonStats.toString())
                .append("\n").append("Інша причина, коменти : ")
                .append(reasonCommentsForOther.toString());
        return new StringResponse(response.toString());
    }
}

