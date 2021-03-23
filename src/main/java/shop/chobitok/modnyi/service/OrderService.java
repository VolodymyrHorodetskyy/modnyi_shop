package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.*;
import shop.chobitok.modnyi.entity.response.GetAllOrderedResponse;
import shop.chobitok.modnyi.entity.response.PaginationInfo;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;
import static shop.chobitok.modnyi.util.StringHelper.splitTTNString;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ShoeRepository shoeRepository;
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

    @Value("${spring.datasource.username}")
    private String username;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientService clientService, NovaPostaService novaPostaService, MailService mailService, CanceledOrderReasonService canceledOrderReasonService, UserRepository userRepository, StatusChangeService statusChangeService, NovaPostaRepository postaRepository, GoogleDocsService googleDocsService, DiscountService discountService, PayedOrderedService payedOrderedService, CardService cardService) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
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
    }

    public Ordered findByTTN(String ttn) {
        return orderRepository.findOneByAvailableTrueAndTtn(ttn);
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String phoneOrName, String model, boolean withoutTTN, String orderBy,
                                        String userId) {
        PageRequest pageRequest = PageRequest.of(page, size, createSort(orderBy));
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, removeSpaces(TTN), phoneOrName, withoutTTN, userId), pageRequest);
        getAllOrderedResponse.setOrderedList(orderedPage.getContent());
        PaginationInfo paginationInfo = new PaginationInfo(orderedPage.getPageable().getPageNumber(), orderedPage.getPageable().getPageSize(), orderedPage.getTotalPages(), orderedPage.getTotalElements());
        getAllOrderedResponse.setPaginationInfo(paginationInfo);
        return getAllOrderedResponse;
    }

    public Ordered getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Ordered> getOrdersByStatus(Status status) {
        updateOrdersByNovaPosta();
        return orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(status));
    }

    private Sort createSort(String orderBy) {
        Sort.Direction direction = Sort.Direction.DESC;
        if ("dateEdited".equals(orderBy)) {
            return Sort.by(direction, "lastModifiedDate");
        } else {
            return Sort.by(direction, "createdDate");
        }
    }


    public Ordered createOrder(CreateOrderRequest createOrderRequest) {
        Ordered ordered = new Ordered();
        if (!StringUtils.isEmpty(createOrderRequest.getTtn())) {
            if (getAll(0, 1, createOrderRequest.getTtn(), null, null, false, null, null).getOrderedList().size() > 0) {
                throw new ConflictException("Замовлення з такою накладною вже існує");
            }
        } else {
            ordered.setWithoutTTN(true);
        }
        if (createOrderRequest.getShoes() != null && createOrderRequest.getShoes().size() > 0) {
            ordered.setOrderedShoes(shoeRepository.findAllById(createOrderRequest.getShoes()));
        } else {
            throw new ConflictException("Взуття не може бути пусте");
        }
        setUser(ordered, createOrderRequest.getUserId());
        ordered.setClient(clientService.createClient(createOrderRequest));
        ordered.setTtn(createOrderRequest.getTtn());
        statusChangeService.createRecord(ordered, ordered.getStatus(), createOrderRequest.getStatus());
        ordered.setStatus(createOrderRequest.getStatus());
        ordered.setSize(createOrderRequest.getSize());
        ordered.setAddress(createOrderRequest.getAddress());
        ordered.setNotes(createOrderRequest.getNotes());
        if (createOrderRequest.isFullpayment()) {
            ordered.setFullPayment(true);
        } else {
            ordered.setPrePayment(createOrderRequest.getPrepayment());
        }
        ordered.setPrice(createOrderRequest.getPrice());
        return orderRepository.save(ordered);
    }

    public Ordered updateOrder(Long id, UpdateOrderRequest updateOrderRequest) {
        Ordered ordered = orderRepository.findById(id).orElse(null);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        setUser(ordered, updateOrderRequest.getUserId());
        if (!StringUtils.isEmpty(updateOrderRequest.getPostComment())) {
            ordered.setPostComment(updateOrderRequest.getPostComment());
        }
        ordered.setDiscount(discountService.getById(updateOrderRequest.getDiscountId()));
        ordered.setUrgent(updateOrderRequest.getUrgent());
        ordered.setFullPayment(updateOrderRequest.isFull_payment());
        ordered.setNotes(updateOrderRequest.getNotes());
        updateShoeAndSize(ordered, updateOrderRequest);
        clientService.updateOrCreateClient(ordered.getClient(), updateOrderRequest);
        ordered.setPrePayment(updateOrderRequest.getPrepayment());
        ordered.setPrice(updateOrderRequest.getPrice());
        statusChangeService.createRecord(ordered, ordered.getStatus(), updateOrderRequest.getStatus());
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
            result.append(importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId())));
        }
        return new StringResponse(result.toString());
    }

    public String importOrderFromTTNString(String ttn, Long userId, Discount discount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ConflictException("User not found");
        }
        StringBuilder result = new StringBuilder();
        if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
            try {
                Ordered ordered = novaPostaService.createOrUpdateOrderFromNP(ttn, null, discount);
                ordered.setUser(user);
                orderRepository.save(ordered);
                if (ordered.getOrderedShoes().size() < 1 || ordered.getSize() == null) {
                    result.append(ttn + "  ... взуття або розмір не визначено \n");
                } else {
                    result.append(ttn + "  ... імпортовано \n");
                }
            } catch (ConflictException e) {
                result.append(ttn + "  ... неможливо знайти ттн \n");
            }
        } else {
            result.append(ttn + "  ... вже існує в базі \n");
        }
        return result.toString();
    }

    public String updateOrdersByStatusesByNovaPosta(List<Status> statuses) {
        return updateOrdersByNovaPosta(orderRepository.findAllByAvailableTrueAndStatusIn(statuses));
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
                updateOrdersByNovaPosta(Arrays.asList(ordered));
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
        return updateOrdersByStatusesByNovaPosta(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО, Status.ЗМІНА_АДРЕСУ));
    }


    private Ordered updateOrderByTrackingEntity(Ordered ordered, Data data) {
        if (data != null && ordered != null && data.getStatusCode() != 1) {
            return updateOrderFields(ordered, checkNewStatusAndReturnStatusCode(data, ordered),
                    data.getRecipientAddress()
                    , data.getRedeliverySum(), cardService.getOrSaveAndGetCardByName(data.getCardMaskedNumber()));
        }
        return ordered;
    }

    private Ordered updateOrderByDataListTrackingEntity(Ordered ordered, DataForList dataForList) {
        if (dataForList == null && ordered != null) {
            return updateOrderFields(ordered, 1, dataForList.getRecipientAddressDescription(),
                    Double.valueOf(dataForList.getBackwardDeliveryMoney()), cardService.getOrSaveAndGetCardByName(dataForList.getRedeliveryPaymentCard()));
        }
        return ordered;
    }

    @Transactional
    private Ordered updateOrderFields(Ordered ordered, Integer statusCode, String recipientAddress
            , Double redeliverySum, Card card) {
        Status oldStatus = ordered.getStatus();
        Status newStatus = convertToStatus(statusCode);
        updateOrderFieldBeforeStatusesCheck(ordered, redeliverySum, card);
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
                if (newStatus == Status.ДОСТАВЛЕНО) {
                    novaPostaService.updateDatePayedKeeping(ordered);
                }
                Client client = ordered.getClient();
                if (client != null) {
                    if (!StringUtils.isEmpty(client.getMail()) && !username.equals("root") && newStatus != Status.ВИДАЛЕНО) {
                        mailService.sendStatusNotificationEmail(client.getMail(), newStatus);
                    }
                }
            }
        }
        return orderRepository.save(ordered);
    }

    private Ordered updateOrderFieldBeforeStatusesCheck(Ordered ordered, Double redeliverySum, Card card) {
        if (redeliverySum != null) {
            ordered.setReturnSumNP(redeliverySum);
        }
        if (card != null) {
            ordered.setCard(card);
        }
        return orderRepository.save(ordered);
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
        List<Ordered> canceledOrdereds = orderRepository.findBystatusNP(103);
        return canceledOrdereds;
    }

    public StringResponse getCanceledString(boolean updateStatuses) {
        StringBuilder result = new StringBuilder();
        List<Ordered> orderedList = getCanceled(updateStatuses);
        for (Ordered ordered : orderedList) {
            result.append(ordered.getTtn() + "\n" + ordered.getPostComment() + "\n\n");
        }
        return new StringResponse(result.toString());
    }

    public StringResponse returnAllCanceled(boolean updateStatuses) {
        StringBuilder result = new StringBuilder();
        List<Ordered> canceledOrdereds = getCanceled(updateStatuses);
        for (Ordered ordered : canceledOrdereds) {
            result.append(novaPostaService.returnCargo(ordered) + "\n");
        }
        return new StringResponse(result.toString());
    }

    private void updateShoeAndSize(Ordered ordered, UpdateOrderRequest updateOrderRequest) {
        ordered.setOrderedShoes(shoeRepository.findAllById(updateOrderRequest.getShoes()));
        ordered.setSize(updateOrderRequest.getSize());
    }

    public Ordered createOrder(Ordered ordered) {
        return orderRepository.save(ordered);
    }

    public List<Ordered> createOrders(List<Ordered> orderedList) {
        return orderRepository.saveAll(orderedList);
    }

    public Ordered addShoeToOrder(AddShoeToOrderRequest addShoeToOrderRequest) {
        Ordered ordered = orderRepository.getOne(addShoeToOrderRequest.getOrderId());
        if (ordered == null) {
            throw new ConflictException("Order not found");
        }
        Shoe shoe = shoeRepository.getOne(addShoeToOrderRequest.getShoeId());
        if (shoe == null) {
            throw new ConflictException("Shoe not found");
        }
        ordered.getOrderedShoes().add(shoe);
        return orderRepository.save(ordered);
    }

    public StringResponse makeAllPayed() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndPayedFalseAndStatusIn(Arrays.asList(Status.ОТРИМАНО));
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoes().size() > 0) {
                ordered.setPayed(true);
            }
        }
        payedOrderedService.makeAllCounted();
        orderRepository.saveAll(orderedList);
        return new StringResponse("готово");
    }


    public void updateGoogleDocsDeliveryFile() {
        googleDocsService.updateDeliveryFile(countNeedDeliveryFromDB(false).getResult());
    }

    public StringResponse countNeedDeliveryFromDB(boolean updateStatuses) {
        StringBuilder stringBuilder = new StringBuilder();
        if (updateStatuses) {
            updateOrdersByStatusesByNovaPosta(Arrays.asList(Status.СТВОРЕНО));
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
                if (!StringUtils.isEmpty(ordered.getTtn())) {
                    result.append(ordered.getSequenceNumber()).append(". ").append(ordered.getTtn()).append("\n").append(ordered.getPostComment()).append("\n\n");
                } else {
                    result.append(ordered.getSequenceNumber()).append(". ").append("без накладноЇ\n");
                    for (Shoe shoe : ordered.getOrderedShoes()) {
                        result.append(shoe.getModel()).append(" ").append(shoe.getColor());
                    }
                    result.append(", ").append(ordered.getSize()).append("\n\n");
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
