package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.request.UpdateOrderedShoeRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.OrderedShoeRepository;

@Service
public class OrderedShoeService {

    private final OrderedShoeRepository orderedShoeRepository;

    public OrderedShoeService(OrderedShoeRepository orderedShoeRepository) {
        this.orderedShoeRepository = orderedShoeRepository;
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
}
