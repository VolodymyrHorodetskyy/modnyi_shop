package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.StatusChangeRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static shop.chobitok.modnyi.entity.Status.ВІДМОВА;
import static shop.chobitok.modnyi.entity.Status.ДОСТАВЛЕНО;
import static shop.chobitok.modnyi.util.DateHelper.formDateTimeFromOrGetDefault;
import static shop.chobitok.modnyi.util.DateHelper.formDateTimeToOrGetDefault;

@Service
public class CheckerService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final AppOrderRepository appOrderRepository;
    private final AppOrderService appOrderService;
    private final StatusChangeRepository statusChangeRepository;
    private final StatisticService statisticService;
    private final ParamsService paramsService;
    private final FinanceService financeService;
    private final UserRepository userRepository;

    @Value("${params.monthlyReceivingPercentage}")
    private String monthlyReceivingPercentage;

    public CheckerService(OrderService orderService, OrderRepository orderRepository, NotificationService notificationService, AppOrderRepository appOrderRepository, AppOrderService appOrderService, StatusChangeRepository statusChangeRepository, StatisticService statisticService, ParamsService paramsService, FinanceService financeService, UserRepository userRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.appOrderRepository = appOrderRepository;
        this.appOrderService = appOrderService;
        this.statusChangeRepository = statusChangeRepository;
        this.statisticService = statisticService;
        this.paramsService = paramsService;
        this.financeService = financeService;
        this.userRepository = userRepository;
    }

    public void checkPayedKeepingOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        orderService.updateOrdersByNovaPosta();
        List<Ordered> arrivedAndDeniedOrders = orderRepository.findAllByStatusInAndDatePayedKeepingNPIsNotNull(
                List.of(ДОСТАВЛЕНО));
        for (Ordered ordered : arrivedAndDeniedOrders) {
            LocalDateTime datePayedKeeping = ordered.getDatePayedKeepingNP();
            if (now().plusDays(3).isAfter(datePayedKeeping)) {
                String userName = "";
                if (ordered.getUser() != null) {
                    userName = ordered.getUser().getName();
                }
                notificationService.createNotification("Платне зберігання з " + datePayedKeeping.format(formatter),
                        userName, MessageType.PAYED_KEEPING, ordered.getTtn());
            }
        }
    }

    public void makeAppOrderNewAgain() {
        makeAppOrdersNew(appOrderRepository.findByStatusIn(asList(AppOrderStatus.Чекаємо_оплату, AppOrderStatus.Не_Відповідає, AppOrderStatus.В_обробці)));
    }

    public void checkRemindOnAppOrdersAndMakeThemNewAgain() {
        makeAppOrdersNew(appOrderRepository.findByRemindOnIsLessThanEqual(now()));
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
        List<StatusChangeRecord> statusChangeRecords = statusChangeRepository.findAllByCreatedDateGreaterThanEqualAndNewStatus(now().minusDays(30), Status.ВІДПРАВЛЕНО);
        for (StatusChangeRecord statusChangeRecord : statusChangeRecords) {
            List<StatusChangeRecord> statusChangeRecordList = statusChangeRepository.findOneByNewStatusInAndOrderedId(asList(ДОСТАВЛЕНО, Status.ОТРИМАНО, ВІДМОВА),
                    statusChangeRecord.getOrdered().getId());
            if ((statusChangeRecordList == null || statusChangeRecordList.size() == 0)
                    && Duration.between(statusChangeRecord.getCreatedDate(), now()).toDays() > 4) {
                notificationService.createNotification("Статус відправлено більше ніж 5 днів",
                        statusChangeRecord.getOrdered().getTtn(), MessageType.NEED_ATTENTION);
            }
        }
    }

    public StringResponse checkAppOrdersBecameOrdersForAllUsers(String from, String to) {
        List<User> users = userRepository.findAll().stream().filter(user -> user.getId() != 1L).collect(Collectors.toList());
        StringBuilder response = new StringBuilder();
        for (User user : users) {
            response.append(checkAppOrdersBecameOrders(user.getId(), from, to).getResult())
                    .append("\n\n");
        }
        return new StringResponse(response.toString());
    }

    public StringResponse checkAppOrdersBecameOrders(Long userId, String from, String to) {
        AppOrderSpecification specification = new AppOrderSpecification();
        LocalDateTime fromDate = formDateTimeFromOrGetDefault(from);
        LocalDateTime toDate = formDateTimeToOrGetDefault(to);
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
        long amountWithoutFakeData = appOrders.stream().filter(
                        appOrder -> !(appOrder.getStatus() == AppOrderStatus.Скасовано
                                && (appOrder.getCancellationReason() == AppOrderCancellationReason.НЕ_ВІРНІ_ДАНІ
                                || appOrder.getCancellationReason() == AppOrderCancellationReason.ДУБЛІКАТ)))
                .count();
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
                .append("\n").append("Кількість заявок з вірними даними : ")
                .append(amountWithoutFakeData)
                .append("\n").append("Кількість заявок з ттн : ")
                .append(amountWithTtn)
                .append("\n").append("% : ")
                .append(appOrders.size() != 0 ? amountWithTtn * 100 / amountWithoutFakeData : 0)
                .append("\n").append("Статистика причин скасування заявок : ")
                .append(cancellationReasonStats)
                .append("\n").append("Інша причина, коменти : ")
                .append(reasonCommentsForOther);
        return new StringResponse(response.toString());
    }

    @Transactional
    public void updateMonthlyReceivingPercentage() {
        paramsService.saveOrChangeParam(monthlyReceivingPercentage,
                valueOf(financeService.getEarnings(now().minusDays(30), now()).getReceivedPercentage()));
    }
}

