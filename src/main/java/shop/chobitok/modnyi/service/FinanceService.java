package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FinanceService {

    private OrderRepository orderRepository;

    public FinanceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public EarningsResponse getEarnings(String dateTime1, String dateTime2) {
        LocalDateTime fromDate = formDateFrom(dateTime1);
        LocalDateTime toDate = formDateTo(dateTime2);
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate));
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
        return new EarningsResponse(fromDate, toDate, sum, predictedSum, received, denied, orderedList.size(), receivedPercentage);
    }

    private LocalDateTime formDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }

    private LocalDateTime formDateFrom(String dateTimeFrom) {
        LocalDateTime localDateTime = formDate(dateTimeFrom);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now().minusDays(7);
        }
        return localDateTime.with(LocalTime.of(0, 0));
    }

    private LocalDateTime formDateTo(String dateTimeTo) {
        LocalDateTime localDateTime = formDate(dateTimeTo);
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }
        localDateTime = localDateTime.with(LocalTime.of(23, 59));
        return localDateTime;
    }

}

