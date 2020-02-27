package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.CreateOrderRequest;
import shop.chobitok.modnyi.entity.request.UpdateOrderRequest;
import shop.chobitok.modnyi.entity.response.GetAllOrderedResponse;
import shop.chobitok.modnyi.entity.response.PaginationInfo;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ShoeRepository shoeRepository;
    private ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository, ShoeRepository shoeRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.clientRepository = clientRepository;
    }

    public GetAllOrderedResponse getAll(int page, int size, String TTN, String model) {
        PageRequest pageRequest = PageRequest.of(page, size);
        GetAllOrderedResponse getAllOrderedResponse = new GetAllOrderedResponse();
        Page orderedPage = orderRepository.findAll(new OrderedSpecification(model, TTN), pageRequest);
        getAllOrderedResponse.setOrderedList(orderedPage.getContent());
        PaginationInfo paginationInfo = new PaginationInfo(orderedPage.getPageable().getPageNumber(), orderedPage.getPageable().getPageSize(), orderedPage.getTotalPages(), orderedPage.getTotalElements());
        getAllOrderedResponse.setPaginationInfo(paginationInfo);
        return getAllOrderedResponse;
    }

    public Ordered getOne(Long id) {
        return orderRepository.getOne(id);
    }

    public Ordered createOrder(CreateOrderRequest createOrderRequest) {
        Ordered ordered = new Ordered();
        Shoe shoe = null;
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
        return orderRepository.save(ordered);
    }

    public Ordered updateOrder(Long id, UpdateOrderRequest updateOrderRequest) {
        Ordered ordered = orderRepository.getOne(id);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        ordered.setNotes(updateOrderRequest.getNotes());
        return orderRepository.save(ordered);
    }

    public Ordered createOrder(Ordered ordered) {
        return orderRepository.save(ordered);
    }

    public List<Ordered> createOrders(List<Ordered> orderedList) {
        return orderRepository.saveAll(orderedList);
    }
}
