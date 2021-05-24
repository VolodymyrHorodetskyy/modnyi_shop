package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static shop.chobitok.modnyi.util.OrderHelper.breakdownByStatuses;

@Service
public class FinanceService {

    private OrderRepository orderRepository;
    private ShoePriceService shoePriceService;

    public FinanceService(OrderRepository orderRepository, ShoePriceService shoePriceService) {
        this.orderRepository = orderRepository;
        this.shoePriceService = shoePriceService;
    }

    public EarningsResponse getEarnings(List<Ordered> orderedList, LocalDateTime fromDate, LocalDateTime toDate) {
        Map<Status, List<Ordered>> statusListMap = breakdownByStatuses(orderedList);
        Double sum = 0d;
        Double predictedSum = 0d;
        Double realisticSum;
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

    public EarningsResponse getEarnings(String dateTime1, String dateTime2) {
        LocalDateTime fromDate = DateHelper.formDateFromOrGetDefault(dateTime1);
        LocalDateTime toDate = DateHelper.formDateToOrGetDefault(dateTime2);
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(fromDate, toDate));
        return getEarnings(orderedList, fromDate, toDate);
    }

    public Double getMargin(List<Ordered> ordereds) {
        Double margin = 0d;
        for (Ordered ordered : ordereds) {
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                ShoePrice shoePrice = shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered);
                margin += shoePrice.getPrice() - shoePrice.getCost();
            }
        }
        return margin;
    }


}

