package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.*;
import shop.chobitok.modnyi.entity.response.GetAllOrderedResponse;
import shop.chobitok.modnyi.entity.response.PaginationInfo;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

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

    @Value("${novaposta.phoneNumber}")
    private String phone;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientRepository clientRepository, StorageService storageService, NovaPostaService novaPostaService) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.clientRepository = clientRepository;
        this.storageService = storageService;
        this.novaPostaService = novaPostaService;
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String phone, String model, boolean withoutTTN, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, size, createSort(orderBy));
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, TTN, phone, withoutTTN), pageRequest);
        getAllOrderedResponse.setOrderedList(orderedPage.getContent());
        PaginationInfo paginationInfo = new PaginationInfo(orderedPage.getPageable().getPageNumber(), orderedPage.getPageable().getPageSize(), orderedPage.getTotalPages(), orderedPage.getTotalElements());
        getAllOrderedResponse.setPaginationInfo(paginationInfo);
        return getAllOrderedResponse;
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
        List<Shoe> shoes = new ArrayList<>();
        shoes.add(shoe);
        ordered.setOrderedShoes(shoes);
        ordered.setClient(client);
        ordered.setTtn(createOrderRequest.getTtn());
        ordered.setStatus(createOrderRequest.getStatus());
        ordered.setSize(createOrderRequest.getSize());
        ordered.setAddress(createOrderRequest.getAddress());
        ordered.setNotes(createOrderRequest.getNotes());
        ordered.setPrePayment(createOrderRequest.getPrepayment());
        ordered.setPrice(createOrderRequest.getPrice());
        ordered.setFromStorage(createOrderRequest.isFromStorage());
        if (createOrderRequest.isFromStorage()) {
            storageService.setStorage(ordered);
        }
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
        String[] splited = request.getTtns().split("\\s+");
        List<String> stringList = new ArrayList<>();
        for (String ttn : splited) {
            if (!StringUtils.isEmpty(ttn) && isNumeric(ttn) && ttn.length() == 14) {
                if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
                    FromNPToOrderRequest fromNPToOrderRequest = new FromNPToOrderRequest();
                    fromNPToOrderRequest.setPhone(phone);
                    fromNPToOrderRequest.setTtn(ttn);
                    try {
                        Ordered ordered = orderRepository.save(novaPostaService.createOrderFromNP(fromNPToOrderRequest));
                        if (ordered.getOrderedShoes().size() < 1 || ordered.getSize() == null) {
                            stringList.add(ttn + "  ... взуття або розмір не визначено \n");
                        } else {
                            stringList.add(ttn + "  ... імпортовано \n");
                        }
                    }catch (ConflictException e){
                        stringList.add(ttn +"  ... неможливо знайти ттн");
                    }
                } else {
                    stringList.add(ttn + "  ... вже існує в базі \n");
                }
            } else {
                stringList.add(ttn + "  ... неможливо знайти ттн \n");
            }
        }
        return new StringResponse(stringList);
    }

    public List<Ordered> createFromTTNListAndSave(FromTTNFileRequest request) {
        return createOrders(novaPostaService.createOrderedFromTTNFile(request));
    }

    public String updateOrderStatuses() {
        List<Status> statuses = Arrays.asList(Status.CREATED, Status.DELIVERED, Status.SENT);
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(statuses);
        StringBuilder result = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (updateStatus(ordered)) {
                result.append(ordered.getTtn() + " ... статус змінено на " + ordered.getStatus() + "\n");
            }
        }
        return result.toString();
    }

    private boolean updateStatus(Ordered ordered) {
        Status newStatus = novaPostaService.getNewStatus(ordered);
        if (ordered.getStatus() != newStatus) {
            ordered.setStatus(newStatus);
            orderRepository.save(ordered);
            return true;
        }
        return false;
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

}
