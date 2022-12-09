package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.request.UpdateOrderedShoeRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.OrderedShoeRepository;

import java.util.List;

@Service
public class OrderedShoeService {

    private final OrderedShoeRepository orderedShoeRepository;
    private final OrderRepository orderRepository;

    public OrderedShoeService(OrderedShoeRepository orderedShoeRepository, OrderRepository orderRepository) {
        this.orderedShoeRepository = orderedShoeRepository;
        this.orderRepository = orderRepository;
    }

    public OrderedShoe getById(Long orderedShoeId) {
        return orderedShoeRepository.findById(orderedShoeId).orElse(null);
    }

    public OrderedShoe updateShoe(UpdateOrderedShoeRequest request) {
        OrderedShoe orderedShoe = orderedShoeRepository.findById(request.getOrderedShoeId())
                .orElseThrow(() -> new ConflictException("OrderedShoe not found"));
        orderedShoe.setShouldNotBePayed(request.getShouldNotBePayed());
        return orderedShoeRepository.save(orderedShoe);
    }

    public OrderedShoe saveOrUpdateOrderedShoe(OrderedShoe orderedShoe) {
        return orderedShoeRepository.save(orderedShoe);
    }

    public List<OrderedShoe> getOrderedShoe(String ttn) {
        return orderRepository.findOneByAvailableTrueAndTtn(ttn).getOrderedShoeList();
    }
}
