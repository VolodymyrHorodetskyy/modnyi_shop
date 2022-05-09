package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.AmountsInfoResponse;
import shop.chobitok.modnyi.entity.response.GoogleChartObject;
import shop.chobitok.modnyi.entity.response.StringDoubleObj;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.service.entity.StatShoe;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.DateHelper.formDateTimeFromOrGetDefault;
import static shop.chobitok.modnyi.util.DateHelper.formDateTimeToOrGetDefault;

@Service
public class StatisticService {

    private final NovaPostaRepository postaRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ShoePriceService shoePriceService;
    private final AppOrderRepository appOrderRepository;
    private final CanceledOrderReasonRepository canceledOrderReasonRepository;
    private final PayedOrderedService payedOrderedService;
    private final ParamsService paramsService;
    private final OurTtnService ourTtnService;
    private final HistoryService historyService;
    private final NPOrderMapper npOrderMapper;


    public StatisticService(NovaPostaRepository postaRepository, OrderRepository orderRepository, OrderService orderService, ShoePriceService shoePriceService, AppOrderRepository appOrderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, PayedOrderedService payedOrderedService, ParamsService paramsService, OurTtnService ourTtnService, HistoryService historyService, NPOrderMapper npOrderMapper) {
        this.postaRepository = postaRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.shoePriceService = shoePriceService;
        this.appOrderRepository = appOrderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.payedOrderedService = payedOrderedService;
        this.paramsService = paramsService;
        this.ourTtnService = ourTtnService;
        this.historyService = historyService;
        this.npOrderMapper = npOrderMapper;
    }

    public StringResponse getIssueOrders() {
        //TODO: optimisation
        StringBuilder result = new StringBuilder();
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoeList() == null || ordered.getOrderedShoeList().size() < 1) {
                result.append(ordered.getTtn()).append("  ... замовлення без взуття або розміру \n");
            }
        }
        if (result.length() == 0) {
            result.append("Помилок немає");
        }
        return new StringResponse(result.toString());
    }

    private Set<String> toTTNSet(List<String> orderedList) {
        Set<String> allTTNSet = new LinkedHashSet<>();
        for (String s : orderedList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        return allTTNSet;
    }

    public StringResponse needToPayed(boolean updateStatuses, Long companyId) {
        Double sum = 0d;
        StringBuilder result = new StringBuilder();
        if (updateStatuses) {
            orderService.updateOrdersByNovaPosta();
        }
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndPayedFalseAndStatusIn(singletonList(Status.ОТРИМАНО));

        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoeList().size() < 1) {
                result.append(ordered.getTtn()).append(" НЕ ВИЗНАЧЕНО\n");
            } else {
                for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                    if (!orderedShoe.isPayed() && orderedShoe.getShoe().getCompany().getId().equals(companyId)) {
                        ShoePrice shoePrice = shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered);
                        if (shoePrice == null) {
                            result.append(ordered.getTtn()).append(" ").append(orderedShoe.getShoe().getModel()).append(" ")
                                    .append(orderedShoe.getShoe().getColor()).append(" - немає ціни\n\n");
                            break;
                        } else {
                            sum += shoePrice.getCost();
                            result.append(ordered.getTtn()).append(" ")
                                    .append(orderedShoe.getShoe().getModelAndColor()).append(" ")
                                    .append(shoePrice.getCost()).append("\n");
                        }
                    }
                }
            }
        }
        Double sumNotCounted = payedOrderedService.getSumNotCounted(companyId);
        result.append("\n").append("Загална сума = ").append(sum).append("\n");
        result.append("Сума відмінених оплачених = ").append(sumNotCounted).append("\n");
        result.append("Сума до оплати = ").append(sum - sumNotCounted);
        return new StringResponse(result.toString());
    }

    static class NeedToBePayed {
        Double sum;
        List<String> ttns;
    }

    public String countAllReceivedAndDenied(String pathAllTTNFile) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathAllTTNFile);
        int received = 0;
        int denied = 0;
        for (String s : allTTNList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(null, s);
            Status status = convertToStatus(trackingEntity.getData().get(0).getStatusCode());
            if (status == Status.ОТРИМАНО) {
                ++received;
            } else if (status == Status.ВІДМОВА) {
                ++denied;
            }
        }
        stringBuilder.append("Отримані = ").append(received);
        stringBuilder.append("\n");
        stringBuilder.append("Відмова = ").append(denied);
        return stringBuilder.toString();
    }


    public String getAllDenied(String pathAllTTNFile, boolean returned) {
        StringBuilder stringBuilderWithDesc = new StringBuilder();
        StringBuilder stringBuilderWithoutDesc = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        stringBuilderWithDesc.append("З описом \n");
        stringBuilderWithoutDesc.append("ТТН \n");
        for (String s : allTTNList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(null, s);
            Data data = trackingEntity.getData().get(0);
            if (data.getStatusCode().equals(103)) {
                stringBuilderWithDesc.append(data.getNumber() + " " + data.getCargoDescriptionString() + "\n");
                stringBuilderWithoutDesc.append(data.getNumber() + "\n");
            }
        }
        stringBuilderWithDesc.append(stringBuilderWithoutDesc.toString());
        return stringBuilderWithDesc.toString();
    }

    public Map<Shoe, Integer> getOrderedShoesStats(String dateFrom, String dateTo, Status status) {
        LocalDateTime fromDate = formDateTimeFromOrGetDefault(dateFrom);
        LocalDateTime toDate = formDateTimeToOrGetDefault(dateTo);
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate, status, true));
        final Map<Shoe, Integer> shoeIntegerMap = countShoesAmount(orderedList);
        final Map<Shoe, Integer> sortedByAmount = shoeIntegerMap.entrySet()
                .stream()
                .sorted((Map.Entry.<Shoe, Integer>comparingByValue().reversed()))
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));
        return sortedByAmount;
    }

    public GoogleChartObject getShoeOrderChart(String dateFrom, String dateTo, Status status) {
        return convertToGoogleChartObject(getOrderedShoesStats(dateFrom, dateTo, status));
    }


    private GoogleChartObject convertToGoogleChartObject(Map<Shoe, Integer> shoeIntegerMap) {
        List<StringDoubleObj> stringDoubleList = new ArrayList<>();
        for (Map.Entry<Shoe, Integer> entry : shoeIntegerMap.entrySet()) {
            StringDoubleObj stringDoubleObj = new StringDoubleObj();
            stringDoubleObj.setString((entry.getKey().getModel() + " " + entry.getKey().getColor()));
            stringDoubleObj.setaDouble(entry.getValue().doubleValue());
            stringDoubleList.add(stringDoubleObj);
        }
        return new GoogleChartObject(stringDoubleList);
    }


    public List<StatShoe> getReceivedPercentage(String dateFrom, String dateTo) {
        LocalDateTime fromDate = formDateTimeFromOrGetDefault(dateFrom);
        LocalDateTime toDate = formDateTimeToOrGetDefault(dateTo);
        List<Ordered> receivedOrderList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate, Status.ОТРИМАНО));
        List<Ordered> deniedOrderList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate, Status.ВІДМОВА));
        final Map<Shoe, Integer> receivedMap = countShoesAmount(receivedOrderList);
        final Map<Shoe, Integer> deniedMap = countShoesAmount(deniedOrderList);

        List<StatShoe> statShoeList = new ArrayList<>();
        for (Map.Entry<Shoe, Integer> entry : receivedMap.entrySet()) {
            Shoe shoe = entry.getKey();
            Integer receivedAmount = entry.getValue();
            Integer deniedAmount = deniedMap.get(shoe);
            if (deniedAmount != null) {
                int generalAmount = receivedAmount + deniedAmount;
                statShoeList.add(new StatShoe(shoe, receivedAmount, deniedAmount, receivedAmount * 100 / generalAmount, generalAmount));
            } else {
                statShoeList.add(new StatShoe(shoe, receivedAmount, 0, 100, receivedAmount));
            }

        }
        statShoeList = statShoeList.stream().sorted(Comparator.comparingInt(StatShoe::getGeneralAmount).reversed()).collect(toList());
        return statShoeList;
    }

    public AmountsInfoResponse countAmounts() {
        int newAppOrdersSize = appOrderRepository.findByStatusIn(singletonList(AppOrderStatus.Новий)).size();
        int canceledWithoutReasonSize = canceledOrderReasonRepository.findByReasonIn(singletonList(CancelReason.НЕ_ВИЗНАЧЕНО)).size();
        int ourTtnsSize = ourTtnService.getTtns(0, 100, false).getNumber();
        int orderMistakes = checkMistakesInOrderAmount(null, null, null);
        return new AmountsInfoResponse(newAppOrdersSize, canceledWithoutReasonSize, ourTtnsSize, orderMistakes);
    }


    public StringResponse getOrdersAndAppordersByPhone(Long id) {
        AppOrder appOrderFromDb = appOrderRepository.findById(id).orElse(null);
        if (appOrderFromDb != null && !isEmpty(appOrderFromDb.getPhone())) {
            StringBuilder result = new StringBuilder();
            if (appOrderFromDb.getPhone().contains("0")) {
                String phone = appOrderFromDb.getPhone().substring(appOrderFromDb.getPhone().indexOf("0"));
                List<Ordered> orderedFromDB = orderRepository.findAll(new OrderedSpecification(phone, appOrderFromDb.getTtn()));
                List<AppOrder> appOrders = appOrderRepository.findAll(new AppOrderSpecification(phone, appOrderFromDb.getId()));
                if (orderedFromDB.size() > 0 || appOrders.size() > 0) {
                    Map<Client, List<Ordered>> clientOrderedMap = new HashMap<>();
                    for (Ordered ordered : orderedFromDB) {
                        List<Ordered> orderedList1 = clientOrderedMap.get(ordered.getClient());
                        if (orderedList1 == null) {
                            orderedList1 = new ArrayList<>();
                            orderedList1.add(ordered);
                            clientOrderedMap.put(ordered.getClient(), orderedList1);
                        } else {
                            orderedList1.add(ordered);
                        }
                    }
                    result.append("Замовлення \n\n");
                    for (Map.Entry<Client, List<Ordered>> entry : clientOrderedMap.entrySet()) {
                        Client client = entry.getKey();
                        result.append(client.getName()).append(" ").append(client.getLastName()).append(" ").append(client.getPhone()).append("\n");
                        for (Ordered ordered : entry.getValue()) {
                            result.append(ordered.getTtn()).append("\n");
                        }
                    }
                    result.append("\n Заявки\n");
                    for (AppOrder appOrder : appOrders) {
                        result.append(appOrder.getId()).append(", ");
                    }
                    return new StringResponse(result.toString());
                }
            }
        }
        return new StringResponse();
    }

    public String getAllOrdersByUser(LocalDateTime from, Long id) {
        StringBuilder builder = new StringBuilder();
        List<Ordered> allReceivedByUser = orderRepository.findAllByAvailableTrueAndUserIdAndStatusAndPayedForUserFalse(id, Status.ОТРИМАНО);
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setUserId(id.toString());
        orderedSpecification.setFrom(from);
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        orderedList.addAll(allReceivedByUser);
        Map<Status, Set<Ordered>> statusListMap = new HashMap<>();
        for (Ordered ordered : orderedList) {
            Set<Ordered> ordereds = statusListMap.get(ordered.getStatus());
            if (ordereds == null) {
                ordereds = new HashSet<>();
                ordereds.add(ordered);
                statusListMap.put(ordered.getStatus(), ordereds);
            } else {
                ordereds.add(ordered);
            }
        }
        builder.append("замовлення з ").append(from).append("\n");
        for (Map.Entry<Status, Set<Ordered>> entry : statusListMap.entrySet()) {
            builder.append(entry.getKey()).append(" = ").append(entry.getValue().size()).append("\n");
        }
        builder.append("\n");
        int notPayed = 0;
        Set<Ordered> received = statusListMap.get(Status.ОТРИМАНО);
        if (received != null) {
            for (Ordered ordered : received) {
                if (!ordered.isPayedForUser() && ordered.getOrderedShoeList() != null && ordered.getOrderedShoeList().size() > 0) {
                    notPayed += ordered.getOrderedShoeList().size();
                }
            }
        }
        builder.append("Не оплаченно за весь час = ").append(notPayed);
        return builder.toString();
    }

    public void payAllForOperator(Long userId) {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndUserIdAndStatusAndPayedForUserFalse(userId, Status.ОТРИМАНО);
        StringBuilder stringBuilder = new StringBuilder();
        for (Ordered ordered : orderedList) {
            ordered.setPayedForUser(true);
            stringBuilder.append(ordered.getTtn()).append("\n");
        }
        historyService.addHistoryRecord(HistoryType.PAYMENT_FOR_OPERATOR, stringBuilder.toString());
        orderRepository.saveAll(orderedList);
    }

    private Map<Shoe, Integer> countShoesAmount(List<Ordered> ordereds) {
        final Map<Shoe, Integer> map = new HashMap<>();
        for (Ordered ordered : ordereds) {
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                Integer amount = map.get(orderedShoe.getShoe());
                if (amount == null) {
                    map.put(orderedShoe.getShoe(), 1);
                } else {
                    map.put(orderedShoe.getShoe(), ++amount);
                }
            }
        }
        return map;
    }

    public StringResponse getRedeliverySumByNpAccountId(Long npAccountId, String dateFrom, String dateTo) {
        paramsService.saveDateFromAndDateToSearchNpAccount(dateFrom, dateTo);
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndDateCreatedGreaterThanAndDateCreatedLessThanAndNpAccountId(
                Arrays.asList(Status.ОТРИМАНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО, Status.СТВОРЕНО),
                formDateTimeFromOrGetDefault(dateFrom), formDateTimeToOrGetDefault(dateTo), npAccountId);
        StringBuilder stringBuilder = new StringBuilder();
        Double sumReceived = 0d;
        Double sumPredicted = 0d;
        for (Ordered ordered : orderedList) {
            if (ordered.getReturnSumNP() != null) {
                if (ordered.getStatus() == Status.ОТРИМАНО) {
                    sumReceived += ordered.getReturnSumNP();
                } else if (ordered.getStatus() == Status.СТВОРЕНО || ordered.getStatus() == Status.ВІДПРАВЛЕНО || ordered.getStatus() == Status.ДОСТАВЛЕНО) {
                    sumPredicted += ordered.getReturnSumNP();
                }
            }
        }
        Double realisticSum = (sumPredicted / 100) * 80;
        stringBuilder.append("Сума отриманих = ").append(sumReceived).append("\n")
                .append("Сума прогнозованих = ").append(sumPredicted).append("\n")
                .append("80% = ").append(realisticSum).append("\n");
        return new StringResponse(stringBuilder.toString());
    }


    public StringResponse checkMistakesInOrder(Long userId, String from, String to) {
        return new StringResponse(checkMistakesInOrderMistakeResponse(userId, from, to).response);
    }

    public int checkMistakesInOrderAmount(Long userId, String from, String to) {
        return checkMistakesInOrderMistakeResponse(userId, from, to).amount;
    }


    private MistakesResponse checkMistakesInOrderMistakeResponse(Long userId, String from, String to) {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        LocalDateTime fromDate = formDateTimeFromOrGetDefault(from);
        LocalDateTime toDate = formDateTimeToOrGetDefault(to);
        orderedSpecification.setFrom(fromDate.minusDays(7));
        orderedSpecification.setTo(toDate);
        orderedSpecification.setStatusNotIn(Collections.singletonList(Status.ВИДАЛЕНО));
        orderedSpecification.setAllCorrect(false);
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
        int mistakesAmount = 0;
        for (Ordered ordered : orderedList) {
            String ttn = ordered.getTtn();
            String userName = ordered.getUser() != null ? ordered.getUser().getName() :
                    "без менеджера";
            if (ordered.getOrderedShoeList() == null || ordered.getOrderedShoeList().size() == 0) {
                if (nullOrderedShoesStringBuilder == null) {
                    nullOrderedShoesStringBuilder = new StringBuilder();
                    nullOrderedShoesStringBuilder.append("Взуття не вибрано").append("\n");

                }
                nullOrderedShoesStringBuilder.append(ttn).append(" ").append(userName).append("\n");
                ++mistakesAmount;
            } else if (ordered.getPrice() < 500) {
                if (priceUnder500StringBuilder == null) {
                    priceUnder500StringBuilder = new StringBuilder();
                    priceUnder500StringBuilder.append("Ціна нижча за 500").append("\n");
                }
                priceUnder500StringBuilder.append(ttn).append(" ").append(userName).append("\n");
                ++mistakesAmount;
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
                    ++mistakesAmount;
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
                    ++mistakesAmount;
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
                    ++mistakesAmount;
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
            response.append(discountIsNull).append("\n");
        }
        if (priceIsNotCorrect != null) {
            response.append(priceIsNotCorrect).append("\n");
        }
        return new MistakesResponse(response.toString(), mistakesAmount);
    }

    private class MistakesResponse {
        public MistakesResponse(String response, int amount) {
            this.response = response;
            this.amount = amount;
        }

        String response;
        int amount;
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
}
