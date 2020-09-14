package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.AmountsInfoResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.service.entity.StatShoe;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;

@Service
public class StatisticService {

    private NovaPostaRepository postaRepository;
    private OrderRepository orderRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;
    private OrderService orderService;
    private ShoePriceService shoePriceService;
    private AppOrderRepository appOrderRepository;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;

    public StatisticService(NovaPostaRepository postaRepository, OrderRepository orderRepository, NPOrderMapper npOrderMapper, NPHelper npHelper, OrderService orderService, ShoePriceService shoePriceService, AppOrderRepository appOrderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository) {
        this.postaRepository = postaRepository;
        this.orderRepository = orderRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
        this.orderService = orderService;
        this.shoePriceService = shoePriceService;
        this.appOrderRepository = appOrderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
    }

    public StringResponse countNeedDeliveryFromDB(boolean updateStatuses) {
        StringBuilder stringBuilder = new StringBuilder();
        if (updateStatuses) {
            orderService.updateOrderStatusesNovaPosta();
        }
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(Status.СТВОРЕНО, false), Sort.by("dateCreated"));
        stringBuilder.append(countNeedDelivery(orderedList));
        stringBuilder.append("Кількість : " + orderedList.size());
        return new StringResponse(stringBuilder.toString());
    }

    private String countNeedDelivery(List<Ordered> orderedList) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        StringBuilder result = new StringBuilder();
        Map<LocalDate, List<Ordered>> localDateOrderedMap = new TreeMap<>();
        for (Ordered ordered : orderedList) {
            addOrderToMap(localDateOrderedMap, ordered);
        }
        for (Map.Entry<LocalDate, List<Ordered>> entry : localDateOrderedMap.entrySet()) {
            result.append(entry.getKey().format(timeFormatter)).append("\n\n");
            for (Ordered ordered : entry.getValue()) {
                if (!StringUtils.isEmpty(ordered.getTtn())) {
                    result.append(ordered.getTtn() + "\n" + ordered.getPostComment() + "\n\n");
                } else {
                    result.append("без накладноЇ\n");
                    for (Shoe shoe : ordered.getOrderedShoes()) {
                        result.append(shoe.getModel()).append(" ").append(shoe.getColor());
                    }
                    result.append(", ").append(ordered.getSize()).append("\n\n");
                }
            }
        }
        return result.toString();
    }

    private void addOrderToMap(Map<LocalDate, List<Ordered>> localDateListMap, Ordered ordered) {
        LocalDate date = ordered.getCreatedDate().toLocalDate();
        List<Ordered> orderedList = localDateListMap.get(date);
        if (orderedList == null) {
            orderedList = new ArrayList<>();
            orderedList.add(ordered);
            localDateListMap.put(date, orderedList);
        } else {
            orderedList.add(ordered);
        }
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


    //TODO refactor, split
    public StringResponse getReturned(boolean excludeFromDeliveryFile) {
        List<Ordered> toSave = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(true, true));
        List<Ordered> orderedList = orderRepository.findByNotForDeliveryFileTrue();
        for (Ordered order : orderedList) {
            order.setNotForDeliveryFile(false);
            toSave.add(order);
        }
        List<Ordered> createdList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        Set<CanceledOrderReason> used = new HashSet<>();
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            result.append(canceledOrderReason.getOrdered().getPostComment()).append("\n").
                    append(canceledOrderReason.getOrdered().getTtn()).append("\n").append(canceledOrderReason.getReturnTtn()).append(" ")
                    .append(canceledOrderReason.getStatus()).append(" ").append(canceledOrderReason.getReason())
                    .append("\n\n");
            if (canceledOrderReason.getReason() == CancelReason.БРАК || canceledOrderReason.getReason() == CancelReason.ЯКІСТЬ) {
                used.add(canceledOrderReason);
            }
        }
        result.append("Звернути увагу\n\n");
        for (CanceledOrderReason canceledOrderReason : used) {
            result.append(canceledOrderReason.getOrdered().getTtn()).append("\n")
                    .append(canceledOrderReason.getReturnTtn()).append(" ").append(canceledOrderReason.getStatus()).append("\n")
                    .append(canceledOrderReason.getReason()).append(" ").append(StringUtils.isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                    .append("\n\n");
        }
        result.append("Співпадіння\n\n");
        for (Ordered ordered : createdList) {
            for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
                if (compareShoeArrays(canceledOrderReason.getOrdered().getOrderedShoes(), ordered.getOrderedShoes()) &&
                        ordered.getSize().equals(canceledOrderReason.getOrdered().getSize()) &&
                        used.add(canceledOrderReason)) {
                    result.append(ordered.getTtn() + " " + ordered.getPostComment() + "\n");
                    result.append(canceledOrderReason.getReturnTtn() + " " + canceledOrderReason.getStatus() + " " + canceledOrderReason.getReason() + "\n\n");
                    if (excludeFromDeliveryFile) {
                        ordered.setNotForDeliveryFile(true);
                        toSave.add(ordered);
                    }
                }
            }
        }
        if (toSave.size() > 0) {
            orderRepository.saveAll(toSave);
        }
        return new StringResponse(result.toString());
    }


    private boolean compareShoeArrays(List<Shoe> shoes, List<Shoe> shoes2) {
        Set<Shoe> shoesSet = new HashSet<>();
        if (shoes.size() != shoes2.size()) {
            return false;
        }
        for (Shoe shoe : shoes) {
            for (Shoe shoe1 : shoes2) {
                if (!(shoe.equals(shoe1) && shoesSet.add(shoe1))) {
                    return false;
                }
            }
        }
        return true;
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
        StringBuilder stringBuilder = new StringBuilder();
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
                TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
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
        return stringBuilder.toString();
    }

    public StringResponse needToPayed(boolean updateStatuses) {
        Map<String, NeedToBePayed> companySumMap = new HashMap<>();
        StringBuilder result = new StringBuilder();
        Double sum = 0d;
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
            result.append("Сума = " + entry.getValue().sum + "\n\n\n");
        }
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
            TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
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
            TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
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
        LocalDateTime fromDate = DateHelper.formDateFrom(dateFrom);
        LocalDateTime toDate = DateHelper.formDateTo(dateTo);
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
        LocalDateTime fromDate = DateHelper.formDateFrom(dateFrom);
        LocalDateTime toDate = DateHelper.formDateTo(dateTo);
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
        statShoeList = statShoeList.stream().sorted(Comparator.comparingInt(StatShoe::getReceivedPercentage).reversed()).collect(toList());
        return statShoeList;
    }

    public AmountsInfoResponse countAmounts() {
        int newAppOrdersSize = appOrderRepository.findByStatusIn(Arrays.asList(AppOrderStatus.Новий)).size();
        int canceledWithoutReasonSize = canceledOrderReasonRepository.findByReasonIn(Arrays.asList(CancelReason.НЕ_ВИЗНАЧЕНО)).size();
        return new AmountsInfoResponse(newAppOrdersSize, canceledWithoutReasonSize);
    }

    public StringResponse getOrdersAndAppordersByPhone(Long id) {
/*        AppOrder appOrderFromDb = appOrderRepository.findById(id).orElse(null);
        if (appOrderFromDb != null) {
            StringBuilder result = new StringBuilder();
            List<Ordered> orderedFromDB = orderRepository.findAll(new OrderedSpecification(appOrderFromDb.getPhone(), appOrderFromDb.getTtn()));
            List<AppOrder> appOrders = appOrderRepository.findAll(new AppOrderSpecification(appOrderFromDb.getPhone(), appOrderFromDb.getId()));
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
        return new StringResponse();*/
        return null;
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


}
