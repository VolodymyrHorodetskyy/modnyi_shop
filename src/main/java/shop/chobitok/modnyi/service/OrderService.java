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
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ShoeRepository shoeRepository;
    private ClientService clientService;
    private StorageService storageService;
    private NovaPostaService novaPostaService;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    private NotificationService notificationService;
    private MailService mailService;
    private CanceledOrderReasonService canceledOrderReasonService;
    private UserRepository userRepository;
    private StatusChangeService statusChangeService;


    @Value("${novaposta.phoneNumber}")
    private String phone;

    @Value("${spring.datasource.username}")
    private String username;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientService clientService, StorageService storageService, NovaPostaService novaPostaService, CanceledOrderReasonRepository canceledOrderReasonRepository, NotificationService notificationService, MailService mailService, CanceledOrderReasonService canceledOrderReasonService, UserRepository userRepository, StatusChangeService statusChangeService) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.clientService = clientService;
        this.storageService = storageService;
        this.novaPostaService = novaPostaService;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.userRepository = userRepository;
        this.statusChangeService = statusChangeService;
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
        ordered.setFullPayment(updateOrderRequest.isFull_payment());
        ordered.setNotes(updateOrderRequest.getNotes());
        updateShoeAndSize(ordered, updateOrderRequest);
        clientService.updateOrCreateClient(ordered.getClient(), updateOrderRequest);
        ordered.setPrePayment(updateOrderRequest.getPrepayment());
        ordered.setPrice(updateOrderRequest.getPrice());
        statusChangeService.createRecord(ordered, ordered.getStatus(), updateOrderRequest.getStatus());
        ordered.setStatus(updateOrderRequest.getStatus());
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
            result.append(importOrderFromTTNString(ttn, request.getUserId()));
        }
        return new StringResponse(result.toString());
    }

    public String importOrderFromTTNString(String ttn, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ConflictException("User not found");
        }
        StringBuilder result = new StringBuilder();
        if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
            FromNPToOrderRequest fromNPToOrderRequest = new FromNPToOrderRequest();
            fromNPToOrderRequest.setPhone(phone);
            fromNPToOrderRequest.setTtn(ttn);
            try {
                Ordered ordered = novaPostaService.createOrderFromNP(fromNPToOrderRequest);
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
        updateAllCanceled();
        return result.toString();
    }

    public String updateOrderStatusesNovaPosta() {
        return updateOrderStatusesNovaPosta(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
    }

    public void updateAllCanceled() {
        List<Ordered> canceled = orderRepository.findBystatusNP(103);
        for (Ordered ordered : canceled) {
            updateCanceled(ordered);
        }
    }

    private void updateCanceled(Ordered ordered) {
        TrackingEntity trackingEntity = novaPostaService.getTrackingEntity(null, ordered.getTtn());
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (!ordered.getStatusNP().equals(data.getStatusCode())) {
                ordered.setStatusNP(data.getStatusCode());
                orderRepository.save(ordered);
            }
        }
    }

    @Transactional
    private boolean updateStatusByNovaPosta(Ordered ordered) {
        TrackingEntity trackingEntity = novaPostaService.getTrackingEntity(null, ordered.getTtn());
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            Status newStatus = convertToStatus(data.getStatusCode());
            Status oldStatus = ordered.getStatus();
            if (oldStatus != newStatus) {
                statusChangeService.createRecord(ordered, oldStatus, newStatus);
                ordered.setStatus(newStatus);
                ordered.setStatusNP(data.getStatusCode());
                orderRepository.save(ordered);
                if (newStatus == Status.ВІДМОВА) {
                    canceledOrderReasonService.createDefaultReasonOnCancel(ordered);
                } else {
                    if (newStatus == Status.ДОСТАВЛЕНО) {
                        orderRepository.save(novaPostaService.updateDatePayedKeeping(ordered));
                    }
                    Client client = ordered.getClient();
                    if (client != null) {
                        if (!StringUtils.isEmpty(client.getMail()) && !username.equals("root")) {
                            mailService.sendStatusNotificationEmail(client.getMail(), newStatus);
                        }
                    }
                }
                return true;
            }
        }
        return false;
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
            result.append(novaPostaService.returnCargo(ordered.getTtn()) + "\n");
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

    public boolean makeAllPayed() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndPayedFalseAndStatusIn(Arrays.asList(Status.ОТРИМАНО));
        for (Ordered ordered : orderedList) {
            ordered.setPayed(true);
        }
        orderRepository.saveAll(orderedList);
        return true;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private List<String> splitTTNString(String ttns) {
        List<String> ttnsList = new ArrayList<>();
        if (ttns != null) {
            String[] ttnsArray = ttns.split("\\s+");
            for (String ttn : ttnsArray) {
                if (!StringUtils.isEmpty(ttn) && isNumeric(ttn) && ttn.length() == 14) {
                    ttnsList.add(ttn);
                }
            }
        }
        return ttnsList;
    }


}
