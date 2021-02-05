package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AdsSpendRec;
import shop.chobitok.modnyi.entity.request.SaveAdsSpends;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.AdsSpendRepository;
import shop.chobitok.modnyi.service.entity.FinanceStats;
import shop.chobitok.modnyi.specification.AdsSpendsSpecification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formDate;

@Service
public class AdsSpendsService {

    private AdsSpendRepository adsSpendRepository;
    private FinanceService financeService;

    public AdsSpendsService(AdsSpendRepository adsSpendRepository, FinanceService financeService) {
        this.adsSpendRepository = adsSpendRepository;
        this.financeService = financeService;
    }

    public List<AdsSpendRec> addOrEditRecord(SaveAdsSpends saveAdsSpends) {
        LocalDate startLocalDate = formDate(saveAdsSpends.getStart());
        LocalDate endLocalDate = formDate(saveAdsSpends.getEnd());
        List<AdsSpendRec> adsSpendRecs = new ArrayList<>();
        checkDates(startLocalDate, endLocalDate);
        if (startLocalDate.isEqual(endLocalDate)) {
            adsSpendRecs.add(saveAdsSpendsRec(startLocalDate, saveAdsSpends.getSpends()));
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
                adsSpendRecs.add(saveAdsSpendsRec(date, amountPerDay));
            }
        }
        return adsSpendRecs;
    }

    public AdsSpendRec saveAdsSpendsRec(LocalDate localDate, Double amount) {
        AdsSpendRec adsSpendRec = adsSpendRepository.findBySpendDate(localDate);
        if (adsSpendRec == null) {
            adsSpendRec = new AdsSpendRec();
            adsSpendRec.setSpendDate(localDate);
        }
        adsSpendRec.setSpendSum(amount);
        return adsSpendRepository.save(adsSpendRec);
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

    private List<AdsSpendRec> getAdsSpendRecs(String from, String to) {
        LocalDate fromLocalDate = formDate(from);
        LocalDate toLocalDate = formDate(to);
        return adsSpendRepository.findAll(new AdsSpendsSpecification(fromLocalDate, toLocalDate));
    }

    public FinanceStats getFinanceStats(List<AdsSpendRec> adsSpendRecList, EarningsResponse earningsResponse) {
        FinanceStats financeStats = new FinanceStats();
        Double sum = earningsResponse.getSum();
        Double predictedSum = earningsResponse.getPredictedSum();
        Double spends = countSpends(adsSpendRecList);
        Double cleanEarning = sum - spends;
        Double projectedEarningMinusSpends = sum + predictedSum - spends;
        return new FinanceStats(sum, predictedSum, earningsResponse.getReceivedPercentage(), sum + predictedSum, spends,
                cleanEarning, projectedEarningMinusSpends);
    }

    public StringResponse getFinanceStatsStringResponse(String from, String to) {
        List<AdsSpendRec> adsSpendRecList = getAdsSpendRecs(from, to);
        FinanceStats financeStats = getFinanceStats(adsSpendRecList, financeService.getEarnings(from, to));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(findMissedDate(formDate(from), formDate(to), adsSpendRecList));
        stringBuilder.append("Дохід : ").append(financeStats.getEarnings()).append("\n")
                .append("Прогнозований дохід : ").append(financeStats.getProjectedEarnings()).append("\n")
                .append("Відсоток отримань : ").append(financeStats.getReceivedPercentage()).append("\n")
                .append("Чистий + прогноз : ").append(financeStats.getEarningsPlusProjected()).append("\n")
                .append("Витрати : ").append(financeStats.getSpends()).append("\n")
                .append("Дохід - витрати : ").append(financeStats.getEarningMinusSpends()).append("\n")
                .append("Дохід + прогноз - витрати : ").append(financeStats.getProjectedEarningsMinusSpends()).append("\n");
        return new StringResponse(stringBuilder.toString());
    }

    private String findMissedDate(LocalDate from, LocalDate to, List<AdsSpendRec> adsSpendRecs) {
        StringBuilder result = new StringBuilder();
        while (true) {
            boolean found = false;
            for (AdsSpendRec adsSpendRec : adsSpendRecs) {
                if (adsSpendRec.getSpendDate().isEqual(from)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.append(from).append(", ");
            }
            if(from.isEqual(to)){
                break;
            }
            from = from.plusDays(1);
        }
        return result.toString();
    }

    private Double countSpends(List<AdsSpendRec> adsSpendRecs) {
        Double amount = 0d;
        for (AdsSpendRec adsSpendRec : adsSpendRecs) {
            amount += adsSpendRec.getSpendSum();
        }
        return amount;
    }


}
