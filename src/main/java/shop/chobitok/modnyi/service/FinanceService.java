package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.dto.NotPayedRecord;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.mapper.NotPayedRecordMapper;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.OrderedShoeRepository;
import shop.chobitok.modnyi.service.entity.NeedToBePayedResponse;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static shop.chobitok.modnyi.util.OrderHelper.breakdownByStatuses;

@Service
public class FinanceService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ShoePriceService shoePriceService;
    private final ParamsService paramsService;
    private final PayedOrderedService payedOrderedService;
    private final OrderedShoeRepository orderedShoeRepository;
    private final NotPayedRecordMapper notPayedRecordMapper;
    private final CompanyFinanceControlService companyFinanceControlService;
    private final CompanyService companyService;

    public FinanceService(OrderService orderService, OrderRepository orderRepository, ShoePriceService shoePriceService, ParamsService paramsService, PayedOrderedService payedOrderedService, OrderedShoeRepository orderedShoeRepository, NotPayedRecordMapper notPayedRecordMapper, CompanyFinanceControlService companyFinanceControlService, CompanyService companyService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.shoePriceService = shoePriceService;
        this.paramsService = paramsService;
        this.payedOrderedService = payedOrderedService;
        this.orderedShoeRepository = orderedShoeRepository;
        this.notPayedRecordMapper = notPayedRecordMapper;
        this.companyFinanceControlService = companyFinanceControlService;
        this.companyService = companyService;
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
            Integer amountReceivedPlusDenied = amountByStatus.get(Status.ОТРИМАНО) + amountByStatus.get(Status.ВІДМОВА);
            if (amountReceivedPlusDenied != 0) {
                receivedPercentage = amountByStatus.get(Status.ОТРИМАНО) * 100 / amountReceivedPlusDenied;
            }
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        int monthlyReceivingPercentage = paramsService.getMonthlyReceivingPercentage();
        realisticSum = (predictedSum / 100) * monthlyReceivingPercentage;
        EarningsResponse earningsResponse = new EarningsResponse(fromDate, toDate, sum, predictedSum, realisticSum, amountByStatus, receivedPercentage, all);
        earningsResponse.setOrderedAmount(orderedList.size());
        earningsResponse.setMonthlyReceivingPercentage(monthlyReceivingPercentage);
        return earningsResponse;
    }

    public EarningsResponse getEarnings(LocalDateTime from, LocalDateTime to) {
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(from, to));
        return getEarnings(orderedList, from, to);
    }

    public EarningsResponse getEarnings(String dateTime1, String dateTime2) {
        LocalDateTime fromDate = DateHelper.formDateTimeFromOrGetDefault(dateTime1);
        LocalDateTime toDate = DateHelper.formDateTimeToOrGetDefault(dateTime2);
        return getEarnings(fromDate, toDate);
    }

    public Double getMargin(List<Ordered> ordereds) {
        Double margin = 0d;
        for (Ordered ordered : ordereds) {
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                ShoePrice shoePrice = shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered);
                if (shoePrice == null) {
                    margin += 0d;
                } else {
                    margin += shoePrice.getPrice() - shoePrice.getCost();
                }
            }
        }
        return margin;
    }

    public NeedToBePayedResponse needToPayed(boolean updateStatuses, Long companyId) {
        Double sum = 0d;
        StringBuilder result = new StringBuilder();
        if (updateStatuses) {
            orderService.updateOrdersByNovaPosta();
        }
        Company company = companyService.getCompany(companyId);
        List<OrderedShoe> orderedShoeList;
        if (company.getAllShouldBePayed() != null && company.getAllShouldBePayed()) {
            orderedShoeList = orderedShoeRepository.findAllByPayedFalseAndShoeCompanyId(companyId);
        }else {
            orderedShoeList = orderedShoeRepository.findAllByStatusReceivedAndPayedFalseAndCompanyId(companyId);
        }
        List<NotPayedRecord> notPayedRecords = new ArrayList<>();
        for (OrderedShoe orderedShoe : orderedShoeList) {
            Ordered ordered = orderRepository.findByOrderedShoeId(orderedShoe.getId());
            if (ordered == null) {
                orderedShoe.setPayed(true);
                orderedShoeRepository.save(orderedShoe);
            } else {
                if (!orderedShoe.isPayed() && orderedShoe.getShoe().getCompany().getId().equals(companyId)) {
                    ShoePrice shoePrice = shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered);
                    if (shoePrice == null) {
                        result.append(ordered.getTtn()).append(" ").append(orderedShoe.getShoe().getModel()).append(" ")
                                .append(orderedShoe.getShoe().getColor()).append(" - немає ціни\n\n");
                    } else {
                        if (orderedShoe.getShouldNotBePayed() == null || !orderedShoe.getShouldNotBePayed()) {
                            sum += shoePrice.getCost();
                            result.append(ordered.getTtn()).append(" ")
                                    .append(orderedShoe.getShoe().getModelAndColor()).append(" ")
                                    .append(shoePrice.getCost()).append("\n");
                            notPayedRecords.add(notPayedRecordMapper.mapTo(ordered.getTtn(), shoePrice.getCost(), orderedShoe));
                        } else {
                            result.append(ordered.getTtn()).append(" ")
                                    .append(orderedShoe.getShoe().getModelAndColor()).append(" ")
                                    .append(" не оплачувати").append("\n");
                            notPayedRecords.add(notPayedRecordMapper.mapTo(ordered.getTtn(), 0D, orderedShoe));
                        }
                    }
                }
            }
        }
        PayedOrderedService.NotPayedRecordsInternalResponse notPayedRecordsResponse = payedOrderedService.getSumNotCounted(companyId);
        notPayedRecords.addAll(notPayedRecordMapper.mapTo(notPayedRecordsResponse.payedOrderedList));
        result.append("\n").append("Загальна сума = ").append(sum).append("\n");
        result.append("Сума відмінених оплачених = ").append(notPayedRecordsResponse.sum).append("\n");
        result.append("Сума до оплати = ").append(sum - notPayedRecordsResponse.sum);
        return new NeedToBePayedResponse(result.toString(), notPayedRecords, sum - notPayedRecordsResponse.sum);
    }

    public StringResponse makePayed(Long companyId, List<NotPayedRecord> notPayedRecords) {
        StringBuilder result = new StringBuilder();
        Double sum = 0d;
        for (NotPayedRecord notPayedRecord : notPayedRecords) {
            if (notPayedRecord.getOrderedShoeId() != null) {
                OrderedShoe orderedShoe = orderedShoeRepository.findById(notPayedRecord.getOrderedShoeId()).orElse(null);
                if (orderedShoe != null && orderedShoe.getShoe() != null) {
                    orderedShoe.setPayed(true);
                    orderedShoeRepository.save(orderedShoe);
                    ShoePrice shoePrice = shoePriceService.getActualShoePrice(orderedShoe.getShoe());
                    result.append(orderedShoe.getShoe().getModelAndColor()).append(" ")
                            .append(" оплачено ").append(shoePrice.getCost()).append("\n");
                    sum += shoePrice.getCost();
                }
            } else if (notPayedRecord.getPayedRecordId() != null) {
                PayedOrdered payedOrdered = payedOrderedService.getById(notPayedRecord.getPayedRecordId());
                if (payedOrdered != null) {
                    payedOrdered.setCounted(true);
                    payedOrderedService.save(payedOrdered);
                    result.append(payedOrdered.getOrdered().getTtn()).append(" враховано ")
                            .append(payedOrdered.getSum()).append("\n");
                    sum -= payedOrdered.getSum();
                }
            }
        }
        if (sum != 0d) {
            companyFinanceControlService.doOperation(new DoCompanyFinanceControlOperationRequest(companyId,
                    sum, result.toString()));
        }
        return new StringResponse(result.toString());
    }
}

