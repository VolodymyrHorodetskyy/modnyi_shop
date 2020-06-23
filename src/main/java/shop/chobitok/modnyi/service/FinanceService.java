package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceService {

    private OrderRepository orderRepository;

    public FinanceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public EarningsResponse getEarnings(String dateTime1, String dateTime2) {
        LocalDateTime fromDate = DateHelper.formDateFrom(dateTime1);
        LocalDateTime toDate = DateHelper.formDateTo(dateTime2);
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate));
        Double sum = 0d;
        Double predictedSum = 0d;
        int received = 0;
        int denied = 0;
        for (Ordered ordered : orderedList) {
            Double cost = 0d;
            if (ordered.getStatus() == Status.ОТРИМАНО) {
                for (Shoe shoe : ordered.getOrderedShoes()) {
                    cost += shoe.getCost();
                    ++received;
                }
                sum = ordered.getPrice() - cost;
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
        return new EarningsResponse(fromDate, toDate, sum, predictedSum, received, denied, orderedList.size(), receivedPercentage);
    }


}

