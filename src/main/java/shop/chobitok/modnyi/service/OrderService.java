package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.ImportOrdersFromStringRequest;
import shop.chobitok.modnyi.entity.request.UpdateOrderRequest;
import shop.chobitok.modnyi.entity.response.GetAllOrderedResponse;
import shop.chobitok.modnyi.entity.response.PaginationInfo;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;
import static shop.chobitok.modnyi.util.StringHelper.splitTTNString;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ClientService clientService;
    private NovaPostaService novaPostaService;
    private MailService mailService;
    private CanceledOrderReasonService canceledOrderReasonService;
    private UserRepository userRepository;
    private StatusChangeService statusChangeService;
    private NovaPostaRepository postaRepository;
    private GoogleDocsService googleDocsService;
    private DiscountService discountService;
    private PayedOrderedService payedOrderedService;
    private CardService cardService;
    private HistoryService historyService;
    private ImportService importService;
    private PropsService propsService;

    @Value("${spring.datasource.username}")
    private String username;

    public OrderService(OrderRepository orderRepository, ClientService clientService, NovaPostaService novaPostaService, MailService mailService, CanceledOrderReasonService canceledOrderReasonService, UserRepository userRepository, StatusChangeService statusChangeService, NovaPostaRepository postaRepository, GoogleDocsService googleDocsService, DiscountService discountService, PayedOrderedService payedOrderedService, CardService cardService, HistoryService historyService, ImportService importService, PropsService propsService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.novaPostaService = novaPostaService;
        this.mailService = mailService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.userRepository = userRepository;
        this.statusChangeService = statusChangeService;
        this.postaRepository = postaRepository;
        this.googleDocsService = googleDocsService;
        this.discountService = discountService;
        this.payedOrderedService = payedOrderedService;
        this.cardService = cardService;
        this.historyService = historyService;
        this.importService = importService;
        this.propsService = propsService;
    }

    public Ordered findByTTN(String ttn) {
        return orderRepository.findOneByAvailableTrueAndTtn(ttn);
    }

    public GetAllOrderedResponse getAll(String TTN, String phoneOrName, String model, boolean withoutTTN,
                                        String userId) {
        return getAll(null, TTN, phoneOrName, model, withoutTTN, userId);
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String phoneOrName, String model, boolean withoutTTN, String orderBy,
                                        String userId) {
        return getAll(PageRequest.of(page, size, createSort(orderBy)), TTN, phoneOrName, model, withoutTTN, userId);
    }

    public GetAllOrderedResponse getAll(PageRequest pageRequest, String TTN, String phoneOrName, String model, boolean withoutTTN,
                                        String userId) {
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        if (pageRequest != null) {
            Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, removeSpaces(TTN), phoneOrName, withoutTTN, userId), pageRequest);
            getAllOrderedResponse.setOrderedList(orderedPage.getContent());
            PaginationInfo paginationInfo = new PaginationInfo(orderedPage.getPageable().getPageNumber(), orderedPage.getPageable().getPageSize(), orderedPage.getTotalPages(), orderedPage.getTotalElements());
            getAllOrderedResponse.setPaginationInfo(paginationInfo);
        } else {
            getAllOrderedResponse.setOrderedList(orderRepository.findAll(new OrderedSpecification(model, removeSpaces(TTN), phoneOrName, withoutTTN, userId)));
        }
        return getAllOrderedResponse;
    }

    public Ordered getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Ordered> getOrdersByStatus(Status status) {
        updateOrdersByNovaPosta();
        return orderRepository.findAllByAvailableTrueAndWithoutTTNFalseAndStatusIn(singletonList(status));
    }

    private Sort createSort(String orderBy) {
        Sort.Direction direction = Sort.Direction.DESC;
        if ("dateEdited".equals(orderBy)) {
            return Sort.by(direction, "lastModifiedDate");
        } else {
            return Sort.by(direction, "createdDate");
        }
    }

    public Ordered updateOrder(Long id, UpdateOrderRequest updateOrderRequest) {
        Ordered ordered = null;
        if (id != null) {
            ordered = orderRepository.findById(id).orElse(null);
        }
        if (ordered == null) {
            ordered = new Ordered();
            ordered.setTtn(String.valueOf(orderRepository.findMaximum() + 1));
        }
        setUser(ordered, updateOrderRequest.getUserId());
        if (!isEmpty(updateOrderRequest.getPostComment()) || ordered.isWithoutTTN()) {
            ordered.setPostComment(updateOrderRequest.getPostComment());
        }
        ordered.setDiscount(discountService.getById(updateOrderRequest.getDiscountId()));
        ordered.setUrgent(updateOrderRequest.getUrgent());
        ordered.setFullPayment(updateOrderRequest.isFull_payment());
        ordered.setNotes(updateOrderRequest.getNotes());
        clientService.updateOrCreateClient(ordered.getClient(), updateOrderRequest);
        ordered.setPrePayment(updateOrderRequest.getPrepayment());
        ordered.setPrice(updateOrderRequest.getPrice());
        if (ordered.isWithoutTTN()) {
            ordered.setStatus(updateOrderRequest.getStatus());
        }
        if (id != null) {
            statusChangeService.createRecord(ordered, ordered.getStatus(), updateOrderRequest.getStatus());
        } else {
            ordered.setNpAccountId(propsService.getActual().getId());
            ordered.setStatus(updateOrderRequest.getStatus());
            ordered.setWithoutTTN(true);
        }
        return orderRepository.save(ordered);
    }

    private void setUser(Ordered ordered, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }
        ordered.setUser(user);
    }

    public StringResponse importOrdersByTTNString(ImportOrdersFromStringRequest request) {
        List<String> splitted = splitTTNString(request.getTtns());
        StringBuilder result = new StringBuilder();
        for (String ttn : splitted) {
            result.append(importService.importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId())));
        }
        return new StringResponse(result.toString());
    }


    public String updateOrdersByStatusesByNovaPosta(List<Status> statuses) {
        return updateOrdersByNovaPosta(orderRepository.findAllByAvailableTrueAndWithoutTTNFalseAndStatusIn(statuses));
    }

    public String updateOrdersByNovaPosta(List<Ordered> orderedList) {
        StringBuilder result = new StringBuilder();
        Map<Long, List<String>> npAndTtns = formMapNpAccountIdAndTtns(orderedList);
        List<Ordered> orderedsStatusCreated = new ArrayList<>();
        result.append(updateStatusNotCreatedOrders(orderedList, npAndTtns));
        for (Ordered ordered : orderedList) {
            if (ordered.getStatus() == Status.СТВОРЕНО) {
                orderedsStatusCreated.add(ordered);
            }
        }
        result.append(updateStatusCreatedOrders(orderedsStatusCreated));
        updateStatus103();
        String resultString = result.toString();
        updateGoogleDocsDeliveryFile();
        return resultString;
    }

    private String updateStatusCreatedOrders(List<Ordered> orderedsStatusCreated) {
        List<DataForList> dataForLists = null;
        StringBuilder result = new StringBuilder();
        for (Ordered ordered : orderedsStatusCreated) {
            if (dataForLists == null) {
                dataForLists = getDataForList(formMapNpAccountIdAndTtns(
                        orderedsStatusCreated.stream().filter(ordered1 -> ordered1.getStatus() == Status.СТВОРЕНО).collect(Collectors.toList())));
            }
            DataForList dataForList = dataForLists.stream().filter(dtf -> dtf.getIntDocNumber().equals(ordered.getTtn())).findFirst().orElse(null);
            if (dataForList != null) {
                updateOrderByDataListTrackingEntity(ordered, dataForList);
                result.append(ordered.getTtn()).append(" ... обновлено ").append("\n");
            } else {
                result.append(ordered.getTtn()).append(" ... не було обновлено ").append("\n");
            }
        }
        return result.toString();
    }

    private String updateStatusNotCreatedOrders(List<Ordered> orderedList, Map<Long, List<String>> npAndTtns) {
        StringBuilder result = new StringBuilder();
        List<Data> dataList = getTrackingEntityByOrders(npAndTtns);
        for (Ordered ordered : orderedList) {
            Data data = dataList.stream().filter(data1 -> data1.getNumber().equals(ordered.getTtn())).findFirst().orElse(null);
            if (data != null) {
                updateOrderByTrackingEntity(ordered, data);
                result.append(ordered.getTtn()).append(" ... обновлено ").append("\n");
            } else {
                result.append(ordered.getTtn()).append(" ... не було обновлено ").append("\n");
            }
        }
        return result.toString();
    }

    private List<DataForList> getDataForList(Map<Long, List<String>> npAndTtns) {
        List<DataForList> dataForList = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : npAndTtns.entrySet()) {
            dataForList.addAll(postaRepository.getTrackingEntityList(15, entry.getKey()).getData());
        }
        return dataForList;
    }

    private List<Data> getTrackingEntityByOrders(Map<Long, List<String>> npAndTtns) {
        List<Data> data = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : npAndTtns.entrySet()) {
            data.addAll(postaRepository.getTrackingByTtns(entry.getKey(), entry.getValue()));
        }
        return data;
    }

    private Map<Long, List<String>> formMapNpAccountIdAndTtns(List<Ordered> orderedList) {
        Map<Long, List<String>> npAndTtns = new HashMap<>();
        for (Ordered ordered : orderedList) {
            Long npAccountId = ordered.getNpAccountId();
            String ttn = ordered.getTtn();
            List<String> ttns = npAndTtns.get(npAccountId);
            if (ttns == null) {
                ttns = new ArrayList<>();
                ttns.add(ttn);
                npAndTtns.put(npAccountId, ttns);
            } else {
                ttns.add(ttn);
            }
        }
        return npAndTtns;
    }


    public void updateCanceled() {
        List<Ordered> canceledAndDeniedOrders = orderRepository
                .findAllByStatusInAndLastModifiedDateGreaterThan(Arrays.asList(Status.ВИДАЛЕНО, Status.ВІДМОВА, Status.ЗМІНА_АДРЕСУ),
                        DateHelper.formLocalDateTimeStartOfTheDay(LocalDateTime.now().minusDays(5)));
        for (Ordered ordered : canceledAndDeniedOrders) {
            CanceledOrderReason canceledOrderReason = canceledOrderReasonService.getCanceledOrderReasonByOrderId(ordered.getId());
            if (canceledOrderReason == null || !canceledOrderReason.isManual()) {
                updateOrdersByNovaPosta(singletonList(ordered));
            }
        }
    }

    public void updateStatus103() {
        List<Ordered> canceled = orderRepository.findBystatusNP(103);
        for (Ordered ordered : canceled) {
            updateCanceled(ordered);
        }
    }

    private void updateCanceled(Ordered ordered) {
        TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (!ordered.getStatusNP().equals(data.getStatusCode())) {
                ordered.setStatusNP(data.getStatusCode());
                orderRepository.save(ordered);
            }
        }
    }

    public String updateOrdersByNovaPosta() {
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.НЕ_ЗНАЙДЕНО, Status.ВІДМОВА), LocalDateTime.now().minusDays(30));
        List<Ordered> toUpdate = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (!(ordered.getStatus() == Status.ВІДМОВА &&
                    canceledOrderReasonService.getCanceledOrderReasonByOrderedId(ordered.getId()).isManual())) {
                toUpdate.add(ordered);
            }
        }
        updateOrdersByNovaPosta(toUpdate);
        return updateOrdersByStatusesByNovaPosta(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО, Status.ЗМІНА_АДРЕСУ));
    }


    private Ordered updateOrderByTrackingEntity(Ordered ordered, Data data) {
        if (data != null && ordered != null && data.getStatusCode() != 1) {
            return updateOrderFields(ordered, checkNewStatusAndReturnStatusCode(data, ordered),
                    data.getRecipientAddress()
                    , data.getRedeliverySum(), cardService.getOrSaveAndGetCardByName(data.getCardMaskedNumber()),
                    ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()),
                    (double) data.getDocumentCost(),
                    data.getStoragePrice() != null ?
                            !data.getStoragePrice().isEmpty() ? Double.valueOf(data.getStoragePrice()) : null : null);
        }
        return ordered;
    }

    private Ordered updateOrderByDataListTrackingEntity(Ordered ordered, DataForList dataForList) {
        if (dataForList != null && ordered != null) {
            return updateOrderFields(ordered, 1, dataForList.getRecipientAddressDescription(),
                    (double) dataForList.getBackwardDeliveryMoney(), cardService.getOrSaveAndGetCardByName(dataForList.getRedeliveryPaymentCard()),
                    null, dataForList.getCostOnSite() != null ?
                            Double.valueOf(dataForList.getCostOnSite()) : null, null);
        }
        return ordered;
    }

    @Transactional
    private Ordered updateOrderFields(Ordered ordered, Integer statusCode, String recipientAddress
            , Double redeliverySum, Card card, LocalDateTime datePayedKeeping, Double deliveryCost,
                                      Double storagePrice) {
        Status oldStatus = ordered.getStatus();
        Status newStatus = convertToStatus(statusCode);
        updateOrderFieldsBeforeStatusesCheck(ordered, redeliverySum, card, datePayedKeeping, deliveryCost,
                storagePrice);
        if (oldStatus != newStatus) {
            statusChangeService.createRecord(ordered, oldStatus, newStatus);
            ordered.setStatus(newStatus);
            ordered.setStatusNP(statusCode);
            if (oldStatus == Status.СТВОРЕНО) {
                ordered.setAddress(recipientAddress);
            }
            orderRepository.save(ordered);
            if (newStatus == Status.ВІДМОВА) {
                canceledOrderReasonService.createDefaultReasonOnCancel(ordered);
            } else {
                Client client = ordered.getClient();
                if (client != null) {
                    if (!isEmpty(client.getMail()) && !username.equals("root") && newStatus != Status.ВИДАЛЕНО) {
                        mailService.sendStatusNotificationEmail(client.getMail(), newStatus);
                    }
                }
            }
        }
        return orderRepository.save(ordered);
    }

    private Ordered updateOrderFieldsBeforeStatusesCheck(Ordered ordered, Double redeliverySum, Card card,
                                                         LocalDateTime datePayedKeeping, Double deliveryCost,
                                                         Double storagePrice) {
        ordered.setDeliveryCost(deliveryCost);
        if (redeliverySum != null) {
            ordered.setReturnSumNP(redeliverySum);
        }
        if (card != null) {
            ordered.setCard(card);
        }
        if (datePayedKeeping != null && (ordered.getDatePayedKeepingNP() == null || !datePayedKeeping.isEqual(ordered.getDatePayedKeepingNP()))) {
            ordered.setDatePayedKeepingNP(datePayedKeeping);
        }
        if (storagePrice != null) {
            ordered.setStoragePrice(storagePrice);
        }
        return ordered;
    }

    private Integer checkNewStatusAndReturnStatusCode(Data data, Ordered ordered) {
        Integer result = null;
        if (data != null) {
            result = data.getStatusCode();
            if (Status.ЗМІНА_АДРЕСУ == convertToStatus(result)) {
                TrackingEntity trackingEntity = postaRepository.getTracking(ordered.getNpAccountId(), data.getLastCreatedOnTheBasisNumber());
                if (trackingEntity.getData().size() > 0) {
                    Data addressChangedTtnData = trackingEntity.getData().get(0);
                    result = addressChangedTtnData.getStatusCode();
                    ordered.setAddressChangeTtn(addressChangedTtnData.getNumber());
                }
            }
            if (data.getStatusCode() == null) {
                result = 2;
            }
        }
        return result;
    }

    public List<Ordered> getCanceled(boolean updateStatuses) {
        if (updateStatuses) {
            updateOrdersByNovaPosta();
        }
        return orderRepository.findBystatusNP(103);
    }

    public StringResponse getCanceledString(boolean updateStatuses) {
        StringBuilder result = new StringBuilder();
        List<Ordered> orderedList = getCanceled(updateStatuses);
        for (Ordered ordered : orderedList) {
            result.append(ordered.getTtn()).append("\n").append(ordered.getPostComment()).append("\n\n");
        }
        return new StringResponse(result.toString());
    }

    public StringResponse returnAllCanceled(boolean updateStatuses) {
        StringBuilder result = new StringBuilder();
        List<Ordered> canceledOrdereds = getCanceled(updateStatuses);
        for (Ordered ordered : canceledOrdereds) {
            String retunrCargoResponse = novaPostaService.returnCargo(ordered);
            result.append(retunrCargoResponse).append("\n");
            historyService.addHistoryRecord(HistoryType.CARGO_RETURN, ordered.getTtn(), retunrCargoResponse);
        }
        return new StringResponse(result.toString());
    }

    public StringResponse makeAllPayed() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndPayedFalseAndStatusIn(singletonList(Status.ОТРИМАНО));
        StringBuilder stringBuilder = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoeList().size() > 0) {
                ordered.setPayed(true);
                stringBuilder.append(ordered.getTtn()).append("\n");
            }
        }
        payedOrderedService.makeAllCounted();
        orderRepository.saveAll(orderedList);
        if (stringBuilder.length() > 0) {
            historyService.addHistoryRecord(HistoryType.PAYMENT_FOR_SHOES, stringBuilder.toString());
        }
        return new StringResponse("готово");
    }


    public void updateGoogleDocsDeliveryFile() {
        //     googleDocsService.updateDeliveryFile(countNeedDeliveryFromDB(false).getResult());
    }

    public StringResponse countNeedDeliveryFromDB(boolean updateStatuses) {
        StringBuilder stringBuilder = new StringBuilder();
        if (updateStatuses) {
            updateOrdersByStatusesByNovaPosta(singletonList(Status.СТВОРЕНО));
        }
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(Status.СТВОРЕНО, false), Sort.by("dateCreated"));
        stringBuilder.append(countNeedDelivery(orderedList));
        stringBuilder.append("Кількість : ").append(orderedList.size());
        return new StringResponse(stringBuilder.toString());
    }


    private String countNeedDelivery(List<Ordered> orderedList) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        StringBuilder result = new StringBuilder();
        Map<LocalDate, List<Ordered>> localDateOrderedMap = new TreeMap<>();
        List<Ordered> toSave = new ArrayList<>();
        List<Ordered> urgent = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (ordered.getUrgent() != null && ordered.getUrgent()) {
                urgent.add(ordered);
            } else {
                addOrderToMap(localDateOrderedMap, ordered);
            }
        }
        if (urgent.size() > 0) {
            result.append("Терміново").append("\n\n");
            for (Ordered ordered : urgent) {
                result.append(ordered.getTtn()).append("\n").append(ordered.getPostComment()).append("\n\n");
            }
        }
        for (Map.Entry<LocalDate, List<Ordered>> entry : localDateOrderedMap.entrySet()) {
            result.append(entry.getKey().format(timeFormatter)).append("\n\n");
            int count = 0;
            List<Ordered> ordereds = entry.getValue();
            for (Ordered ordered : ordereds) {
                if (ordered.getSequenceNumber() != null && ordered.getSequenceNumber() >= count) {
                    count = ordered.getSequenceNumber();
                }
            }
            for (Ordered ordered : ordereds) {
                if (ordered.getSequenceNumber() == null) {
                    ordered.setSequenceNumber(++count);
                    toSave.add(ordered);
                }
                if (!isEmpty(ordered.getTtn()) && ordered.getTtn().length() > 10) {
                    result.append(ordered.getSequenceNumber()).append(". ").append(ordered.getTtn()).append("\n").append(ordered.getPostComment()).append("\n\n");
                } else if (!isEmpty(ordered.getPostComment())) {
                    result.append(ordered.getSequenceNumber()).append(". ").append("без накладної\n");
                    result.append(ordered.getPostComment()).append("\n\n");
                } else if (ordered.getOrderedShoeList() != null && ordered.getOrderedShoeList().size() > 0) {
                    result.append(ordered.getSequenceNumber()).append(". ").append("без накладної\n");
                    for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                        result.append(orderedShoe.getShoe().getModel()).append(" ").append(orderedShoe.getShoe().getColor())
                                .append(", розмір: ").append(orderedShoe.getSize()).append("\n\n");
                    }
                }
            }
        }
        orderRepository.saveAll(toSave);
        return result.toString();
    }


    private boolean addOrderToMap(Map<LocalDate, List<Ordered>> localDateListMap, Ordered ordered) {
        LocalDate date = ordered.getCreatedDate().toLocalDate();
        List<Ordered> orderedList = localDateListMap.get(date);
        if (orderedList == null) {
            orderedList = new ArrayList<>();
            orderedList.add(ordered);
            localDateListMap.put(date, orderedList);
        } else {
            orderedList.add(ordered);
        }
        return true;
    }

}
