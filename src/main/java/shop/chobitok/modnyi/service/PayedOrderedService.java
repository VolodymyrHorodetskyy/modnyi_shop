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

import static java.util.stream.Collectors.toList;

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

    public NotPayedRecordsInternalResponse getSumNotCounted(Long companyId) {
        Double sum = 0d;
        List<PayedOrdered> payedOrderedList = payedOrderedRepository.findByCountedFalse();
        payedOrderedList = payedOrderedList.stream()
                .filter(p -> p.getOrderedShoe().getShoe().getCompany().getId().equals(companyId))
                .collect(toList());
        for (PayedOrdered payedOrdered : payedOrderedList) {
            if (payedOrdered.getOrderedShoe().getShoe().getCompany().getId().equals(companyId)) {
                sum += payedOrdered.getSum();
            }
        }
        return new NotPayedRecordsInternalResponse(sum, payedOrderedList);
    }

    static class NotPayedRecordsInternalResponse {
        Double sum;
        List<PayedOrdered> payedOrderedList;

        public NotPayedRecordsInternalResponse(Double sum, List<PayedOrdered> payedOrderedList) {
            this.sum = sum;
            this.payedOrderedList = payedOrderedList;
        }
    }

    public PayedOrdered getById(Long id) {
        return payedOrderedRepository.findById(id).orElse(null);
    }

    public PayedOrdered save(PayedOrdered payedOrdered) {
        return payedOrderedRepository.save(payedOrdered);
    }
}
