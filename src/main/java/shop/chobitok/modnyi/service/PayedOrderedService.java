package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.PayedOrdered;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.PayedOrderedRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PayedOrderedService {

    private final PayedOrderedRepository payedOrderedRepository;
    private final ShoePriceService shoePriceService;

    public PayedOrderedService(PayedOrderedRepository payedOrderedRepository, ShoePriceService shoePriceService) {
        this.payedOrderedRepository = payedOrderedRepository;
        this.shoePriceService = shoePriceService;
    }

    @Transactional
    public void createPayedOrdered(Ordered ordered) {
        if (ordered.getOrderedShoeList().isEmpty()) {
            throw new ConflictException("в замовленні взуття не вибрано");
        }
        for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
            if (orderedShoe.isPayed()) {
                PayedOrdered payedOrdered = new PayedOrdered();
                payedOrdered.setOrdered(ordered);
                ShoePrice shoePrice = shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered);
                if (shoePrice == null) {
                    throw new ConflictException("Немає ціни для взуття id = " + orderedShoe.getShoe().getId());
                }
                payedOrdered.setSum(shoePrice.getCost());
                payedOrdered.setOrderedShoe(orderedShoe);
                payedOrderedRepository.save(payedOrdered);
            }
        }
    }

    public Double getSumNotCounted(Long companyId) {
        Double sum = 0d;
        List<PayedOrdered> payedOrderedList = payedOrderedRepository.findByCountedFalse();
        for (PayedOrdered payedOrdered : payedOrderedList) {
            if (payedOrdered.getOrderedShoe().getShoe().getCompany().getId().equals(companyId)) {
                sum += payedOrdered.getSum();
            }
        }
        return sum;
    }

    public String makeAllCounted(Long companyId) {
        StringBuilder stringBuilder = new StringBuilder();
        List<PayedOrdered> payedOrderedList = payedOrderedRepository.findByCountedFalse();
        stringBuilder.append("Були оплачені");
        for (PayedOrdered payedOrdered : payedOrderedList) {
            if (payedOrdered.getOrderedShoe().getShoe().getCompany().getId().equals(companyId)) {
                stringBuilder.append(payedOrdered.getOrdered().getTtn()).append(" ")
                        .append(payedOrdered.getOrderedShoe().getShoe().getModel()).append(" ")
                        .append(payedOrdered.getOrderedShoe().getShoe().getColor()).append(" ")
                        .append(payedOrdered.getSum()).append("\n");
                payedOrdered.setCounted(true);
            }
        }
        payedOrderedRepository.saveAll(payedOrderedList);
        return stringBuilder.toString();
    }

}
