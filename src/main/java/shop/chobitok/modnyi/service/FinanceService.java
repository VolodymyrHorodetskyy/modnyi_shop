package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {

    private OrderRepository orderRepository;
    private ShoePriceService shoePriceService;

    public FinanceService(OrderRepository orderRepository, ShoePriceService shoePriceService) {
        this.orderRepository = orderRepository;
        this.shoePriceService = shoePriceService;
    }

    public EarningsResponse getEarnings(String dateTime1, String dateTime2) {
        Map<Status, List<Ordered>> statusListMap = new HashMap<>();
        for (Status status : Status.values()) {
            statusListMap.put(status, new ArrayList<>());
        }
        LocalDateTime fromDate = DateHelper.formDateFrom(dateTime1);
        LocalDateTime toDate = DateHelper.formDateTo(dateTime2);
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate));
        for (Ordered ordered : orderedList) {
            statusListMap.get(ordered.getStatus()).add(ordered);
        }

        Double sum = 0d;
        Double predictedSum = 0d;
        Double realisticSum = 0d;
        int all = 0;
        Map<Status, Integer> amountByStatus = new HashMap<>();
        for (Map.Entry<Status, List<Ordered>> statusListEntry : statusListMap.entrySet()) {
            Status status = statusListEntry.getKey();
            List<Ordered> ordereds = statusListEntry.getValue();
            if (status == Status.СТВОРЕНО || status == Status.ВІДПРАВЛЕНО || status == Status.ДОСТАВЛЕНО) {
                predictedSum += getMargin(ordereds);
            }
            if (status == Status.ОТРИМАНО) {
                sum = getMargin(ordereds);
            }
            amountByStatus.put(status, ordereds.size());
            all += ordereds.size();
        }
        int receivedPercentage = 0;
        try {
            receivedPercentage = amountByStatus.get(Status.ОТРИМАНО) * 100 / (amountByStatus.get(Status.ОТРИМАНО) + amountByStatus.get(Status.ВІДМОВА));
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        realisticSum = (predictedSum / 100) * receivedPercentage;

        return new EarningsResponse(fromDate, toDate, sum, predictedSum, realisticSum, amountByStatus, receivedPercentage, all);
    }

    public Double getMargin(List<Ordered> ordereds) {
        Double margin = 0d;
        for (Ordered ordered : ordereds) {
            for (Shoe shoe : ordered.getOrderedShoes()) {
                ShoePrice shoePrice = shoePriceService.getShoePrice(shoe, ordered);
                margin += shoePrice.getPrice() - shoePrice.getCost();
            }
        }
        return margin;
    }


}

