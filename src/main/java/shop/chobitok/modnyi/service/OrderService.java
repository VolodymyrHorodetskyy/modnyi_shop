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
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.StringHelper;

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


    @Value("${novaposta.phoneNumber}")
    private String phone;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientService clientService, StorageService storageService, NovaPostaService novaPostaService, CanceledOrderReasonRepository canceledOrderReasonRepository, NotificationService notificationService, MailService mailService, CanceledOrderReasonService canceledOrderReasonService) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.clientService = clientService;
        this.storageService = storageService;
        this.novaPostaService = novaPostaService;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.canceledOrderReasonService = canceledOrderReasonService;
    }

    public Ordered findByTTN(String ttn) {
        return orderRepository.findOneByAvailableTrueAndTtn(ttn);
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String phoneOrName, String model, boolean withoutTTN, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, size, createSort(orderBy));
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, removeSpaces(TTN), phoneOrName, withoutTTN), pageRequest);
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
            if (getAll(0, 1, createOrderRequest.getTtn(), null, null, false, null).getOrderedList().size() > 0) {
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
        ordered.setClient(clientService.createClient(createOrderRequest));
        ordered.setTtn(createOrderRequest.getTtn());
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
        Ordered ordered = orderRepository.getOne(id);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        ordered.setFullPayment(updateOrderRequest.isFull_payment());
        ordered.setNotes(updateOrderRequest.getNotes());
        updateShoeAndSize(ordered, updateOrderRequest);
        clientService.updateOrCreateClient(ordered.getClient(), updateOrderRequest);
        ordered.setPrePayment(updateOrderRequest.getPrepayment());
        ordered.setPrice(updateOrderRequest.getPrice());
        ordered.setStatus(updateOrderRequest.getStatus());
        return orderRepository.save(ordered);
    }

    public StringResponse importOrdersByTTNString(ImportOrdersFromStringRequest request) {
        List<String> splitted = splitTTNString(request.getTtns());
        StringBuilder result = new StringBuilder();
        for (String ttn : splitted) {
            result.append(importOrderFromTTNString(ttn));
        }
        return new StringResponse(result.toString());
    }

    public String importOrderFromTTNString(String ttn) {
        StringBuilder result = new StringBuilder();
        if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
            FromNPToOrderRequest fromNPToOrderRequest = new FromNPToOrderRequest();
            fromNPToOrderRequest.setPhone(phone);
            fromNPToOrderRequest.setTtn(ttn);
            try {
                Ordered ordered = orderRepository.save(novaPostaService.createOrderFromNP(fromNPToOrderRequest));
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

    public String updateOrderStatusesNovaPosta() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        StringBuilder result = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (updateStatusByNovaPosta(ordered)) {
                result.append(ordered.getTtn() + " ... статус змінено на " + ordered.getStatus() + "\n");
            }
        }
        updateAllCanceled();
        return result.toString();
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
                if (oldStatus == Status.СТВОРЕНО && newStatus != Status.ВИДАЛЕНО) {
                    novaPostaService.createOrUpdateOrderFromNP(ordered);
                }
                ordered.setStatus(newStatus);
                ordered.setStatusNP(data.getStatusCode());
                orderRepository.save(ordered);
                if (newStatus == Status.ВІДМОВА) {
                    notificationService.createNotification("Клієнт відмовився", "", MessageType.ORDER_BECOME_CANCELED, ordered.getTtn());
                    canceledOrderReasonService.createDefaultReasonOnCancel(ordered);
                } else {
                    Client client = ordered.getClient();
                    if (client != null) {
                        if (!StringUtils.isEmpty(client.getMail())) {
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

    public CanceledOrderReason getCanceledOrderReason(Long orderedId) {
        return canceledOrderReasonRepository.findFirstByOrderedId(orderedId);
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
