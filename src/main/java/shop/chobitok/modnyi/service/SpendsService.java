package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.DaySpendRec;
import shop.chobitok.modnyi.entity.SpendRec;
import shop.chobitok.modnyi.entity.request.SaveAdsSpends;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.DaySpendRepository;
import shop.chobitok.modnyi.repository.SpendRecRepository;
import shop.chobitok.modnyi.service.entity.FinanceStats;
import shop.chobitok.modnyi.specification.AdsSpendsSpecification;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formDate;

@Service
public class SpendsService {

    private DaySpendRepository daySpendRepository;
    private FinanceService financeService;
    private SpendRecRepository spendRecRepository;
    private ParamsService paramsService;

    public SpendsService(DaySpendRepository daySpendRepository, FinanceService financeService, SpendRecRepository spendRecRepository, ParamsService paramsService) {
        this.daySpendRepository = daySpendRepository;
        this.financeService = financeService;
        this.spendRecRepository = spendRecRepository;
        this.paramsService = paramsService;
    }

    @Transactional
    public List<DaySpendRec> addOrEditRecord(SaveAdsSpends saveAdsSpends) {
        LocalDate startLocalDate = formDate(saveAdsSpends.getStart());
        LocalDate endLocalDate = formDate(saveAdsSpends.getEnd());
        checkDates(startLocalDate, endLocalDate);
        SpendRec spendRec = saveSpendRec(saveAdsSpends, startLocalDate, endLocalDate);
        List<DaySpendRec> daySpendRecs = new ArrayList<>();
        if (startLocalDate.isEqual(endLocalDate)) {
            daySpendRecs.add(saveDaySpendRec(startLocalDate, saveAdsSpends.getSpends(), spendRec));
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
            Double amountPerDay = saveAdsSpends.getSpends() / localDates.size();
            for (LocalDate date : localDates) {
                daySpendRecs.add(saveDaySpendRec(date, amountPerDay, spendRec));
            }
        }
        return daySpendRecs;
    }

    public DaySpendRec saveDaySpendRec(LocalDate localDate, Double amount, SpendRec spendRec) {
        DaySpendRec daySpendRec = daySpendRepository.findBySpendDate(localDate);
        if (daySpendRec == null) {
            daySpendRec = new DaySpendRec();
            daySpendRec.setSpendRecords(Arrays.asList(spendRec));
            daySpendRec.setSpendDate(localDate);
            daySpendRec.setSpendSum(amount);
        } else {
            daySpendRec.setSpendSum(daySpendRec.getSpendSum() + amount);
            List<SpendRec> spendRecs = daySpendRec.getSpendRecords();
            if (spendRecs == null) {
                spendRecs = new ArrayList<>();
            }
            spendRecs.add(spendRec);
            daySpendRec.setSpendRecords(spendRecs);
        }
        return daySpendRepository.save(daySpendRec);
    }

    public SpendRec saveSpendRec(SaveAdsSpends saveAdsSpends, LocalDate from, LocalDate to) {
        return spendRecRepository.save(new SpendRec(from, to, saveAdsSpends));
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

    private List<DaySpendRec> getAdsSpendRecs(String from, String to) {
        LocalDate fromLocalDate = formDate(from);
        LocalDate toLocalDate = formDate(to);
        return daySpendRepository.findAll(new AdsSpendsSpecification(fromLocalDate, toLocalDate));
    }


    public FinanceStats getFinanceStats(List<DaySpendRec> daySpendRecList, EarningsResponse earningsResponse) {
        Double sum = earningsResponse.getSum();
        Double realisticSum = earningsResponse.getRealisticSum();
        Double spends = countSpends(daySpendRecList);
        Double cleanEarning = sum - spends;
        Double projectedEarningMinusSpends = sum + realisticSum - spends;
        FinanceStats financeStats = new FinanceStats(sum, realisticSum, earningsResponse.getReceivedPercentage(), sum + realisticSum, spends,
                cleanEarning, projectedEarningMinusSpends, earningsResponse.getMonthlyReceivingPercentage());
        financeStats.setOrderedAmount(earningsResponse.getOrderedAmount());
        financeStats.setRealisticEarning(earningsResponse.getRealisticSum());
        return financeStats;
    }

    public StringResponse getFinanceStatsStringResponse(String from, String to) {
        List<DaySpendRec> daySpendRecList = getAdsSpendRecs(from, to);
        FinanceStats financeStats = getFinanceStats(daySpendRecList, financeService.getEarnings(from, to));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(findMissedDate(formDate(from), formDate(to), daySpendRecList));
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

    private String findMissedDate(LocalDate from, LocalDate to, List<DaySpendRec> daySpendRecs) {
        StringBuilder result = new StringBuilder();
        while (true) {
            boolean found = false;
            for (DaySpendRec daySpendRec : daySpendRecs) {
                if (daySpendRec.getSpendDate().isEqual(from)) {
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

    private Double countSpends(List<DaySpendRec> daySpendRecs) {
        Double amount = 0d;
        for (DaySpendRec daySpendRec : daySpendRecs) {
            amount += daySpendRec.getSpendSum();
        }
        return amount;
    }


}
