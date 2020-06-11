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
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ShoeRepository shoeRepository;
    private ClientRepository clientRepository;
    private StorageService storageService;
    private NovaPostaService novaPostaService;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;

    @Value("${novaposta.phoneNumber}")
    private String phone;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientRepository clientRepository, StorageService storageService, NovaPostaService novaPostaService, CanceledOrderReasonRepository canceledOrderReasonRepository) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.clientRepository = clientRepository;
        this.storageService = storageService;
        this.novaPostaService = novaPostaService;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String phone, String model, boolean withoutTTN, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, size, createSort(orderBy));
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, removeSpaces(TTN), phone, withoutTTN), pageRequest);
        getAllOrderedResponse.setOrderedList(orderedPage.getContent());
        PaginationInfo paginationInfo = new PaginationInfo(orderedPage.getPageable().getPageNumber(), orderedPage.getPageable().getPageSize(), orderedPage.getTotalPages(), orderedPage.getTotalElements());
        getAllOrderedResponse.setPaginationInfo(paginationInfo);
        return getAllOrderedResponse;
    }

    private String removeSpaces(String s) {
        return s.replaceAll("\\s+", "");
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
        Shoe shoe;
        if (createOrderRequest.getShoe() != null) {
            shoe = shoeRepository.getOne(createOrderRequest.getShoe());
            if (shoe == null) {
                throw new ConflictException("Взуття не може бути пусте");
            }
        } else {
            throw new ConflictException("Взуття не може бути пусте");
        }

        List<Shoe> shoes = new ArrayList<>();
        shoes.add(shoe);
        ordered.setOrderedShoes(shoes);
        ordered.setClient(createClient(createOrderRequest));
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

    @Transactional
    public Ordered cancelOrder(CancelOrderRequest cancelOrderRequest) {
        Ordered ordered = orderRepository.getOne(cancelOrderRequest.getOrderId());
        if (ordered == null) {
            throw new ConflictException("Немає такого замовлення");
        }
        ordered.setStatus(Status.ВІДМОВА);
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(cancelOrderRequest.getOrderId());
        if (canceledOrderReason == null) {
            canceledOrderReason = new CanceledOrderReason(ordered, cancelOrderRequest.getReason(), cancelOrderRequest.getComment());
        } else {
            canceledOrderReason.setComment(cancelOrderRequest.getComment());
            canceledOrderReason.setReason(cancelOrderRequest.getReason());
        }
        canceledOrderReasonRepository.save(canceledOrderReason);
        orderRepository.save(ordered);
        return ordered;
    }

    private Client createClient(CreateOrderRequest createOrderRequest) {
        //TODO : make possibility to edit user
        List<Client> clients = clientRepository.findByPhone(createOrderRequest.getPhone());
        Client client = null;
        if (clients.size() > 0) {
            client = clients.get(0);
        } else {
            client = new Client();
            client.setPhone(createOrderRequest.getPhone());
            client.setName(createOrderRequest.getName());
            client.setLastName(createOrderRequest.getLastName());
            client.setMiddleName(createOrderRequest.getMiddleName());
            client = clientRepository.save(client);
        }
        return client;
    }

    public Ordered updateOrder(Long id, UpdateOrderRequest updateOrderRequest) {
        Ordered ordered = orderRepository.getOne(id);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        ordered.setFullPayment(updateOrderRequest.isFull_payment());
        ordered.setNotes(updateOrderRequest.getNotes());
        updateShoeAndSize(ordered, updateOrderRequest);
        Client client = ordered.getClient();
        if (client == null) {
            client = new Client();
            ordered.setClient(client);
        }
        client.setMiddleName(updateOrderRequest.getMiddleName());
        client.setName(updateOrderRequest.getName());
        client.setLastName(updateOrderRequest.getLastName());
        client.setPhone(updateOrderRequest.getPhone());
        clientRepository.save(client);
        ordered.setPrePayment(updateOrderRequest.getPrepayment());
        ordered.setPrice(updateOrderRequest.getPrice());
        return orderRepository.save(ordered);
    }

    public StringResponse importOrdersByTTNString(ImportOrdersFromStringRequest request) {
        List<String> splited = splitTTNString(request.getTtns());
        StringBuilder result = new StringBuilder();
        for (String ttn : splited) {
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
        }
        return new StringResponse(result.toString());
    }

    public List<Ordered> createFromTTNListAndSave(FromTTNFileRequest request) {
        return createOrders(novaPostaService.createOrderedFromTTNFile(request));
    }

    public String updateOrderStatuses() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО, Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        StringBuilder result = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (updateStatus(ordered)) {
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


    private boolean updateStatus(Ordered ordered) {
        TrackingEntity trackingEntity = novaPostaService.getTrackingEntity(null, ordered.getTtn());
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            Status newStatus = ShoeUtil.convertToStatus(data.getStatusCode());
            if (ordered.getStatus() != newStatus) {
                ordered.setStatus(newStatus);
                ordered.setStatusNP(data.getStatusCode());
                orderRepository.save(ordered);
                return true;
            }
        }
        return false;
    }

    public List<Ordered> getCanceled(boolean updateStatuses) {
        if (updateStatuses) {
            updateOrderStatuses();
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
        List<Shoe> shoes = ordered.getOrderedShoes();
        if (shoes != null && shoes.size() > 0) {
            shoes.remove(0);
        } else if (shoes == null) {
            shoes = new ArrayList<>();
        }
        shoes.add(shoeRepository.getOne(updateOrderRequest.getShoe()));
        ordered.setOrderedShoes(shoes);
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
