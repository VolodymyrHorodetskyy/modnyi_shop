package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.AmountsInfoResponse;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.DateHelper.*;

@Service
public class StatisticService {

    private NovaPostaRepository postaRepository;
    private OrderRepository orderRepository;
    private NPOrderMapper npOrderMapper;
    private OrderService orderService;
    private ShoePriceService shoePriceService;
    private AppOrderRepository appOrderRepository;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    private PayedOrderedService payedOrderedService;
    private ParamsService paramsService;

    public StatisticService(NovaPostaRepository postaRepository, OrderRepository orderRepository, NPOrderMapper npOrderMapper, OrderService orderService, ShoePriceService shoePriceService, AppOrderRepository appOrderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, PayedOrderedService payedOrderedService, ParamsService paramsService) {
        this.postaRepository = postaRepository;
        this.orderRepository = orderRepository;
        this.npOrderMapper = npOrderMapper;
        this.orderService = orderService;
        this.shoePriceService = shoePriceService;
        this.appOrderRepository = appOrderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.payedOrderedService = payedOrderedService;
        this.paramsService = paramsService;
    }

    public StringResponse getIssueOrders() {
        //TODO: optimisation
        StringBuilder result = new StringBuilder();
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoes() == null || ordered.getOrderedShoes().size() < 1 || ordered.getSize() == null) {
                result.append(ordered.getTtn() + "  ... замовлення без взуття або розміру \n");
            }
        }
        if (result.length() == 0) {
            result.append("Помилок немає");
        }
        return new StringResponse(result.toString());
    }

    private Set<String> readFileToTTNSet(String path) {
        List<String> allTTNList = ShoeUtil.readTXTFile(path);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        return allTTNSet;
    }

    private Set<String> readFileToTTNSet(MultipartFile file) {
        return toTTNSet(ShoeUtil.readTXTFile(file));
    }

    private Set<String> toTTNSet(List<String> orderedList) {
        Set<String> allTTNSet = new LinkedHashSet<>();
        for (String s : orderedList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        return allTTNSet;
    }

    public String needToBePayed(String pathToAllTTNFile, String payedTTNFile) {
        return null;
/*        StringBuilder stringBuilder = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathToAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        List<String> payedTTNList = ShoeUtil.readTXTFile(payedTTNFile);
        Set<String> payedTTNSet = new HashSet();
        for (String s : payedTTNList) {
            payedTTNSet.add(s.replaceAll("\\s+", ""));
        }

        List<Ordered> orderedList = new ArrayList<>();
        Double sum = 0d;
        for (String s : allTTNSet) {
            if (!payedTTNSet.contains(s)) {
                TrackingEntity trackingEntity = postaRepository.getTracking(null, s);
                Data data = trackingEntity.getData().get(0);
                if (convertToStatus(data.getStatusCode()) == Status.ОТРИМАНО) {
                    stringBuilder.append(data.getNumber());
                    stringBuilder.append("\n");
                    Ordered ordered = npOrderMapper.toOrdered(trackingEntity);
                    if (ordered.getOrderedShoes().size() > 0) {
                        for (Shoe shoe : ordered.getOrderedShoes()) {
                            sum += shoePriceService.getShoePrice(shoe, ordered).getCost();
                        }
                    }
                    orderedList.add(ordered);
                }
            }
        }
        stringBuilder.append("\n\n");
        stringBuilder.append("Сума = " + sum);
        return stringBuilder.toString();*/
    }

    public StringResponse needToPayed(boolean updateStatuses) {
        Map<String, NeedToBePayed> companySumMap = new HashMap<>();
        StringBuilder result = new StringBuilder();
        if (updateStatuses) {
            orderService.updateOrderStatusesNovaPosta();
        }
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndPayedFalseAndStatusIn(Arrays.asList(Status.ОТРИМАНО));

        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoes().size() < 1) {
                result.append(ordered.getTtn() + " НЕ ВИЗНАЧЕНО\n");
            } else {
                for (Shoe shoe : ordered.getOrderedShoes()) {
                    NeedToBePayed needToBePayed = companySumMap.get(shoe.getCompany().getName());
                    if (needToBePayed == null) {
                        needToBePayed = new NeedToBePayed();
                        needToBePayed.sum = shoePriceService.getShoePrice(shoe, ordered).getCost();
                        needToBePayed.ttns = new ArrayList<>();
                        needToBePayed.ttns.add(ordered.getTtn());
                        companySumMap.put(shoe.getCompany().getName(), needToBePayed);
                    } else {
                        needToBePayed.sum += shoePriceService.getShoePrice(shoe, ordered).getCost();
                        needToBePayed.ttns.add(ordered.getTtn());
                    }
                }
            }
        }
        for (Map.Entry<String, NeedToBePayed> entry : companySumMap.entrySet()) {
            result.append(entry.getKey() + "\n\n");
            for (String s : entry.getValue().ttns) {
                result.append(s + "\n");
            }
            result.append("Сума = " + entry.getValue().sum + "\n");
        }
        Double sumNotCounted = payedOrderedService.getSumNotCounted();
        Double sum = 0d;
        if (companySumMap.entrySet().size() > 0) {
            Map.Entry<String, NeedToBePayed> entry = companySumMap.entrySet().iterator().next();
            sum = entry.getValue().sum;
        }
        result.append("Сума відмінених оплачених = ").append(sumNotCounted).append("\n");
        result.append("Сума до оплати = ").append(sum - sumNotCounted);
        return new StringResponse(result.toString());
    }

    class NeedToBePayed {
        Double sum;
        List<String> ttns;
    }

    public String countAllReceivedAndDenied(String pathAllTTNFile) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
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
        stringBuilder.append("Отримані = " + received);
        stringBuilder.append("\n");
        stringBuilder.append("Відмова = " + denied);
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

    public Map<Shoe, Integer> getSoldShoes(String dateFrom, String dateTo, Status status) {
        LocalDateTime fromDate = formDateFromOrGetDefault(dateFrom);
        LocalDateTime toDate = formDateToOrGetDefault(dateTo);
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

    public List<StatShoe> getReceivedPercentage(String dateFrom, String dateTo) {
        LocalDateTime fromDate = formDateFromOrGetDefault(dateFrom);
        LocalDateTime toDate = formDateToOrGetDefault(dateTo);
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
                Integer generalAmount = receivedAmount + deniedAmount;
                statShoeList.add(new StatShoe(shoe, receivedAmount, deniedAmount, receivedAmount * 100 / generalAmount, generalAmount));
            } else {
                statShoeList.add(new StatShoe(shoe, receivedAmount, 0, 100, receivedAmount));
            }

        }
        statShoeList = statShoeList.stream().sorted(Comparator.comparingInt(StatShoe::getGeneralAmount).reversed()).collect(toList());
        return statShoeList;
    }

    public AmountsInfoResponse countAmounts() {
        int newAppOrdersSize = appOrderRepository.findByStatusIn(Arrays.asList(AppOrderStatus.Новий)).size();
        int canceledWithoutReasonSize = canceledOrderReasonRepository.findByReasonIn(Arrays.asList(CancelReason.НЕ_ВИЗНАЧЕНО)).size();
        return new AmountsInfoResponse(newAppOrdersSize, canceledWithoutReasonSize);
    }


    public StringResponse getOrdersAndAppordersByPhone(Long id) {
        AppOrder appOrderFromDb = appOrderRepository.findById(id).orElse(null);
        if (appOrderFromDb != null) {
            StringBuilder result = new StringBuilder();
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
                    result.append(client.getName() + " " + client.getLastName() + " " + client.getPhone() + "\n");
                    for (Ordered ordered : entry.getValue()) {
                        result.append(ordered.getTtn() + "\n");
                    }
                }
                result.append("\n Заявки\n");
                for (AppOrder appOrder : appOrders) {
                    result.append(appOrder.getId() + ", ");
                }
                return new StringResponse(result.toString());
            }
        }
        return new StringResponse();
    }

    public StringResponse getAllOrdersByUser(Long id) {
        StringBuilder builder = new StringBuilder();
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndUserId(id);
        Map<Status, List<Ordered>> statusListMap = new HashMap<>();
        for (Ordered ordered : orderedList) {
            List<Ordered> ordereds = statusListMap.get(ordered.getStatus());
            if (ordereds == null) {
                ordereds = new ArrayList<>();
                ordereds.add(ordered);
                statusListMap.put(ordered.getStatus(), ordereds);
            } else {
                ordereds.add(ordered);
            }
        }
        for (Map.Entry<Status, List<Ordered>> entry : statusListMap.entrySet()) {
            builder.append(entry.getKey()).append(" = ").append(entry.getValue().size()).append("\n");
        }
        builder.append("\n");
        int notPayed = 0;
        List<Ordered> received = statusListMap.get(Status.ОТРИМАНО);
        for (Ordered ordered : received) {
            if (!ordered.isPayedForUser()) {
                ++notPayed;
            }
        }
        builder.append("Не оплаченно = " + notPayed);
        return new StringResponse(builder.toString());
    }

    public void payAllForOperator(Long userId) {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndUserIdAndStatus(userId, Status.ОТРИМАНО);
        for (Ordered ordered : orderedList) {
            ordered.setPayedForUser(true);
        }
        orderRepository.saveAll(orderedList);
    }

    private Map<Shoe, Integer> countShoesAmount(List<Ordered> ordereds) {
        final Map<Shoe, Integer> map = new HashMap<>();
        for (Ordered ordered : ordereds) {
            for (Shoe shoe : ordered.getOrderedShoes()) {
                Integer amount = map.get(shoe);
                if (amount == null) {
                    map.put(shoe, 1);
                } else {
                    map.put(shoe, ++amount);
                }
            }
        }
        return map;
    }

    public StringResponse getRedeliverySumByNpAccountId(Long npAccountId, String dateFrom, String dateTo) {
        paramsService.saveDateFromAndDateToSearchNpAccount(dateFrom, dateTo);
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndDateCreatedGreaterThanAndDateCreatedLessThanAndNpAccountId(
                Arrays.asList(Status.ОТРИМАНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО, Status.СТВОРЕНО),
                formDateFromOrGetDefault(dateFrom), formDateToOrGetDefault(dateTo), npAccountId);
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


}
