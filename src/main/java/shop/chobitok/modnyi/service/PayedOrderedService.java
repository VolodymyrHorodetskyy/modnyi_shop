package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.PayedOrdered;
import shop.chobitok.modnyi.repository.PayedOrderedRepository;

import java.util.List;

@Service
public class PayedOrderedService {

    private PayedOrderedRepository payedOrderedRepository;
    private ShoePriceService shoePriceService;

    public PayedOrderedService(PayedOrderedRepository payedOrderedRepository, ShoePriceService shoePriceService) {
        this.payedOrderedRepository = payedOrderedRepository;
        this.shoePriceService = shoePriceService;
    }

    public PayedOrdered createPayedOrdered(Ordered ordered) {
        if (!ordered.isPayed() || payedOrderedRepository.findByTtn(ordered.getTtn()) != null) {
            return null;
        }
        PayedOrdered payedOrdered = new PayedOrdered();
        payedOrdered.setTtn(ordered.getTtn());
        Double sum = 0d;
        for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
            sum += shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered).getCost();
        }
        payedOrdered.setSum(sum);
        return payedOrderedRepository.save(payedOrdered);
    }

    public Double getSumNotCounted() {
        Double sum = 0d;
        List<PayedOrdered> payedOrderedList = payedOrderedRepository.findByCountedFalse();
        for (PayedOrdered payedOrdered : payedOrderedList) {
            sum += payedOrdered.getSum();
        }
        return sum;
    }

    public void makeAllCounted() {
        List<PayedOrdered> payedOrderedList = payedOrderedRepository.findByCountedFalse();
        for (PayedOrdered payedOrdered : payedOrderedList) {
            payedOrdered.setCounted(true);
        }
        payedOrderedRepository.saveAll(payedOrderedList);
    }

}
