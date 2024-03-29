package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.response.IncomeReport;
import shop.chobitok.modnyi.entity.response.OrderDetail;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IncomeCalculatorService {

    private final OrderRepository orderRepository;
    private final ShoePriceService shoePriceService;
    private final ParamsService paramsService;
    private final CostsService costsService;

    public IncomeCalculatorService(OrderRepository orderRepository, ShoePriceService shoePriceService, ParamsService paramsService, CostsService costsService) {
        this.orderRepository = orderRepository;
        this.shoePriceService = shoePriceService;
        this.paramsService = paramsService;
        this.costsService = costsService;
    }

    public IncomeReport calculateIncome(LocalDateTime from, LocalDateTime to, boolean showOrderDetails, boolean showCostsDetails) {
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(from, to));

        double receivedIncome = 0.0;
        double receivedIncomeMinusCosts = 0.0;
        double potentialIncome = 0.0;

        List<OrderDetail> orderDetailsListReceived = new ArrayList<>();
        List<OrderDetail> orderDetailsListPotential = new ArrayList<>();
        List<String> missingPrices = new ArrayList<>();
        List<DayCosts> costsDetailsList = new ArrayList<>();

        double totalCosts = costsService.getAdsSpendRecs(from.toLocalDate(), to.toLocalDate())
                .stream()
                .mapToDouble(DayCosts::getSpendSum)
                .sum();

        int approvalPercentage = paramsService.getMonthlyReceivingPercentage();

        for (Ordered order : orderedList) {
            double orderTotalPrice = order.getPrice();
            double orderShoesCost = order.getOrderedShoeList().stream()
                    .mapToDouble(shoe -> {
                        ShoePrice shoePrice = shoePriceService.getShoePrice(shoe.getShoe(), order);
                        if (shoePrice == null) {
                            missingPrices.add("Model: " + shoe.getShoe().getModel() + ", Color: " + shoe.getShoe().getColor());
                            return 0.0;
                        }
                        return shoePrice.getCost();
                    })
                    .sum();

            if (order.getStatus() == Status.ОТРИМАНО) {
                receivedIncome += orderTotalPrice - orderShoesCost;
                receivedIncomeMinusCosts = receivedIncome - totalCosts;
                addToOrderDetailsList(order, orderDetailsListReceived, orderTotalPrice, orderShoesCost, showOrderDetails);
            } else if (order.getStatus() == Status.СТВОРЕНО || order.getStatus() == Status.ВІДПРАВЛЕНО || order.getStatus() == Status.ДОСТАВЛЕНО) {
                potentialIncome += (orderTotalPrice - orderShoesCost) * approvalPercentage / 100.0;
                addToOrderDetailsList(order, orderDetailsListPotential, orderTotalPrice, orderShoesCost, showOrderDetails);
            }
        }

        if (showCostsDetails) {
            costsDetailsList.addAll(costsService.getAdsSpendRecs(from.toLocalDate(), to.toLocalDate()));
        }

        double potentialIncomePlusReceivedIncomeMinusCosts = receivedIncome + potentialIncome - totalCosts;

        return new IncomeReport(receivedIncome, receivedIncomeMinusCosts,
                potentialIncome, potentialIncomePlusReceivedIncomeMinusCosts,
                orderDetailsListReceived, orderDetailsListPotential,
                costsDetailsList, missingPrices, approvalPercentage);
    }

    private void addToOrderDetailsList(Ordered order, List<OrderDetail> orderDetailList, double price, double cost, boolean showOrderDetails) {
        if (showOrderDetails) {
            StringBuilder orderedShoesStringBuilder = new StringBuilder();
            for (OrderedShoe shoe : order.getOrderedShoeList()) {
                orderedShoesStringBuilder.append(shoe.getShoe().getModelAndColor()).append("; ");
            }
            orderDetailList.add(new OrderDetail(order.getTtn(), orderedShoesStringBuilder.toString(), price, cost));
        }
    }
}