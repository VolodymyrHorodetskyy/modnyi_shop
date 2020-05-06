package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FinanceService {

    private OrderRepository orderRepository;

    public FinanceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public EarningsResponse getEarnings(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        dateTime1 = dateTime1.with(LocalTime.of(0, 0));
        dateTime2 = dateTime2.with(LocalTime.of(0, 0));
        List<Ordered> orderedList = orderRepository.findByDateCreatedGreaterThanEqualAndDateCreatedLessThanEqual(dateTime1, dateTime2);
        Double sum = 0d;
        Double predictedSum = 0d;
        int received = 0;
        int denied = 0;
        for (Ordered ordered : orderedList) {
            if (ordered.getStatus() == Status.ОТРИМАНО) {
                for (Shoe shoe : ordered.getOrderedShoes()) {
                    sum += shoe.getPrice() - shoe.getCost();
                    ++received;
                }
            } else if (ordered.getStatus() == Status.ВІДМОВА) {
                ++denied;
            } else if (ordered.getStatus() == Status.СТВОРЕНО || ordered.getStatus() == Status.ВІДПРАВЛЕНО || ordered.getStatus() == Status.ДОСТАВЛЕНО) {
                for (Shoe shoe : ordered.getOrderedShoes()) {
                    predictedSum += shoe.getPrice() - shoe.getCost();
                }
            }
        }
        int receivedPercentage = 0;
        try {
            receivedPercentage = received * 100 / (received + denied);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return new EarningsResponse(dateTime1, dateTime2, sum, predictedSum, received, denied, orderedList.size(), receivedPercentage);
    }

}

