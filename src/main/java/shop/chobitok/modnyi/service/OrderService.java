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

    public List<Ordered> getOrdersByStatus(Status status) {
        updateOrderStatusesNovaPosta();
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

    public String updateOrderStatusesNovaPosta(List<Status> statuses) {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(statuses);
        StringBuilder result = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (updateStatusByNovaPosta(ordered)) {
                result.append(ordered.getTtn() + " ... статус змінено на " + ordered.getStatus() + "\n");
            }
        }
        updateStatus103();
        String resultString = result.toString();
        updateGoogleDocsDeliveryFile();
        return resultString;
    }

    public void updateCanceled() {
        List<Ordered> canceledAndDeniedOrders = orderRepository
                .findAllByStatusInAndLastModifiedDateGreaterThan(Arrays.asList(Status.ВИДАЛЕНО, Status.ВІДМОВА, Status.ЗМІНА_АДРЕСУ),
                        DateHelper.formLocalDateTimeStartOfTheDay(LocalDateTime.now().minusDays(5)));
        for (Ordered ordered : canceledAndDeniedOrders) {
            CanceledOrderReason canceledOrderReason = canceledOrderReasonService.getCanceledOrderReasonByOrderId(ordered.getId());
            if (canceledOrderReason == null || !canceledOrderReason.isManual()) {
                updateStatusByNovaPosta(ordered);
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

    public String updateOrderStatusesNovaPosta() {
        return updateOrderStatusesNovaPosta(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО, Status.ЗМІНА_АДРЕСУ));
    }


    @Transactional
    private boolean updateStatusByNovaPosta(Ordered ordered) {
        TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            updateOrderedFields(ordered, data);
            Status newStatus = checkNewStatus(data, ordered, convertToStatus(data.getStatusCode()));
            Status oldStatus = ordered.getStatus();
            if (oldStatus != newStatus) {
                statusChangeService.createRecord(ordered, oldStatus, newStatus);
                ordered.setStatus(newStatus);
                ordered.setStatusNP(data.getStatusCode());
                if (oldStatus == Status.СТВОРЕНО) {
                    ordered.setAddress(data.getRecipientAddress());
                }
                orderRepository.save(ordered);
                if (newStatus == Status.ВІДМОВА) {
                    canceledOrderReasonService.createDefaultReasonOnCancel(ordered);
                } else {
                    if (newStatus == Status.ДОСТАВЛЕНО) {
                        orderRepository.save(novaPostaService.updateDatePayedKeeping(ordered));
                    }
                    Client client = ordered.getClient();
                    if (client != null) {
                        if (!StringUtils.isEmpty(client.getMail()) && !username.equals("root") && newStatus != Status.ВИДАЛЕНО) {
                            mailService.sendStatusNotificationEmail(client.getMail(), newStatus);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private Ordered updateOrderedFields(Ordered ordered, Data data) {
        if (ordered.getReturnSumNP() != null && !ordered.getReturnSumNP().equals(data.getRedeliverySum())) {
            ordered.setReturnSumNP(data.getRedeliverySum());
            ordered.setCard(cardService.getOrSaveAndGetCardByName(data.getCardMaskedNumber()));
            ordered = orderRepository.save(ordered);
        }
        return ordered;
    }

    private Status checkNewStatus(Data data, Ordered ordered, Status newStatus) {
        Status toReturn = newStatus;
        if (newStatus == Status.ЗМІНА_АДРЕСУ) {
            TrackingEntity trackingEntity = postaRepository.getTracking(ordered.getNpAccountId(), data.getLastCreatedOnTheBasisNumber());
            if (trackingEntity.getData().size() > 0) {
                toReturn = convertToStatus(trackingEntity.getData().get(0).getStatusCode());
            }
        }
        if (newStatus == null) {
            toReturn = Status.ВИДАЛЕНО;
        }
        return toReturn;
    }

    public List<Ordered> getCanceled(boolean updateStatuses) {
        if (updateStatuses) {
            updateOrderStatusesNovaPosta();
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
            updateOrderStatusesNovaPosta(Arrays.asList(Status.СТВОРЕНО));
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
