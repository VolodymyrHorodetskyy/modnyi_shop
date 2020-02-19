package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.repository.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Ordered> getAll(int page, int size, String TTN, String model) {
        return orderRepository.findAll();
    }

    public Ordered getOne(Long id) {
        return orderRepository.getOne(id);
    }

    public Ordered createOrder(Ordered ordered) {
        return orderRepository.save(ordered);
    }
}
