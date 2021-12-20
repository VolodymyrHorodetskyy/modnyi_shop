package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Costs;
import shop.chobitok.modnyi.entity.DayCosts;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.CostsRepository;
import shop.chobitok.modnyi.repository.DayCostsRepository;
import shop.chobitok.modnyi.service.entity.FinanceStats;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formDate;

@Service
public class CostsService {

    private FinanceService financeService;
    private CostsRepository costsRepository;
    private DayCostsRepository dayCostsRepository;
    private VariantsService variantsService;

    public CostsService(FinanceService financeService, CostsRepository costsRepository, DayCostsRepository dayCostsRepository, VariantsService variantsService) {
        this.financeService = financeService;
        this.costsRepository = costsRepository;
        this.dayCostsRepository = dayCostsRepository;
        this.variantsService = variantsService;
    }

    public List<DayCosts> addOrEditRecord(SaveAdsSpendsRequest saveAdsSpendsRequest) {
        LocalDate startLocalDate = formDate(saveAdsSpendsRequest.getStart());
        LocalDate endLocalDate = formDate(saveAdsSpendsRequest.getEnd());
        Variants spendType = variantsService.getById(saveAdsSpendsRequest.getSpendTypeId());
        return addOrEditRecord(startLocalDate, endLocalDate, spendType, saveAdsSpendsRequest);
    }

    @Transactional
    public List<DayCosts> addOrEditRecord(LocalDate startLocalDate, LocalDate endLocalDate,
                                          Variants spendType, SaveAdsSpendsRequest saveAdsSpendsRequest) {
        checkDates(startLocalDate, endLocalDate);
        Costs costs = saveSpendRec(saveAdsSpendsRequest, spendType, startLocalDate, endLocalDate);
        List<DayCosts> dayCostsList = new ArrayList<>();
        if (startLocalDate.isEqual(endLocalDate)) {
            dayCostsList.add(saveDaySpendRec(costs, startLocalDate, saveAdsSpendsRequest.getSpends(),
                    spendType, saveAdsSpendsRequest.getDescription()));
        } else {
            List<LocalDate> localDates = new ArrayList<>();
            LocalDate temp = startLocalDate;
            while (true) {
                localDates.add(temp);
                temp = temp.plusDays(1);
                if (temp.isEqual(endLocalDate)) {
                    localDates.add(temp);
                    break;
                }
            }
            Double amountPerDay = saveAdsSpendsRequest.getSpends() / localDates.size();
            for (LocalDate date : localDates) {
                dayCostsList.add(saveDaySpendRec(costs, date, amountPerDay, spendType,
                        saveAdsSpendsRequest.getDescription()));
            }
        }
        return dayCostsList;
    }

    public DayCosts saveDaySpendRec(Costs costs, LocalDate localDate, Double amount, Variants spendType,
                                    String comment) {
        return dayCostsRepository.save(new DayCosts(localDate, amount, spendType, comment, costs));
    }

    public Costs saveSpendRec(SaveAdsSpendsRequest saveAdsSpendsRequest, Variants spendType, LocalDate from, LocalDate to) {
        return costsRepository.save(new Costs(from, to, spendType, saveAdsSpendsRequest));
    }

    private boolean checkDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new ConflictException("Start or End date should not be null");
        }
        if (start.isAfter(end)) {
            throw new ConflictException("Date start cannot be greater than end");
        }
        return true;
    }

    private List<DayCosts> getAdsSpendRecs(LocalDate fromLocalDate, LocalDate toLocalDate) {
        //TODO: кастиль
        return dayCostsRepository
                .findAllBySpendDateGreaterThanEqualAndSpendDateLessThanEqual(fromLocalDate,
                        toLocalDate);
    }


    public FinanceStats getFinanceStats(List<DayCosts> dayCostsList, EarningsResponse earningsResponse) {
        Double sum = earningsResponse.getSum();
        Double realisticSum = earningsResponse.getRealisticSum();
        Double spends = countSpends(dayCostsList);
        Double cleanEarning = sum - spends;
        Double projectedEarningMinusSpends = sum + realisticSum - spends;
        FinanceStats financeStats = new FinanceStats(sum, earningsResponse.getPredictedSum(), earningsResponse.getReceivedPercentage(), sum + realisticSum, spends,
                cleanEarning, projectedEarningMinusSpends, earningsResponse.getMonthlyReceivingPercentage());
        financeStats.setOrderedAmount(earningsResponse.getOrderedAmount());
        financeStats.setRealisticEarning(earningsResponse.getRealisticSum());
        return financeStats;
    }

    public StringResponse getFinanceStatsStringResponse(String from, String to) {
        LocalDate fromLocalDate = formDate(from);
        LocalDate toLocalDate = formDate(to);
        List<DayCosts> daySpendRecList = getAdsSpendRecs(fromLocalDate, toLocalDate);
        FinanceStats financeStats = getFinanceStats(daySpendRecList, financeService.getEarnings(from, to));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(findMissedDate(fromLocalDate, toLocalDate, daySpendRecList));
        stringBuilder.append("Дохід : ").append(financeStats.getEarnings()).append("\n")
                .append("Прогнозований дохід : ").append(financeStats.getProjectedEarnings()).append("\n")
                .append("Реальний прогнозований дохід : ").append(financeStats.getRealisticEarning()).append("\n")
                .append("Відсоток отримань : ").append(financeStats.getReceivedPercentage()).append("\n")
                .append("Місячний відсоток отримань: ").append(financeStats.getMonthlyReceivingPercentage()).append("\n")
                .append("Дохід + прогноз : ").append(financeStats.getEarningsPlusProjected()).append("\n")
                .append("Витрати : ").append(financeStats.getSpends()).append("\n")
                .append("Кількість замовлень : ").append(financeStats.getOrderedAmount()).append("\n")
                .append("Ціна замовлення : ").append(financeStats.getSpends() / financeStats.getOrderedAmount()).append("\n")
                .append("Дохід - витрати : ").append(financeStats.getEarningMinusSpends()).append("\n")
                .append("Дохід + прогноз - витрати : ").append(financeStats.getProjectedEarningsMinusSpends()).append("\n");
        return new StringResponse(stringBuilder.toString());
    }

    private String findMissedDate(LocalDate from, LocalDate to, List<DayCosts> dayCostsList) {
        StringBuilder result = new StringBuilder();
        while (true) {
            boolean found = false;
            for (DayCosts dayCosts : dayCostsList) {
                if (dayCosts.getSpendDate().isEqual(from)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.append(from).append(", ");
            }
            if (from.isEqual(to)) {
                break;
            }
            from = from.plusDays(1);
        }
        if (result.length() > 0) {
            result.append("\n");
        }
        return result.toString();
    }

    private Double countSpends(List<DayCosts> dayCostsList) {
        Double amount = 0d;
        for (DayCosts dayCosts : dayCostsList) {
            amount += dayCosts.getSpendSum();
        }
        return amount;
    }
}
