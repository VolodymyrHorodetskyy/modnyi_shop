package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AdsSpendRec;
import shop.chobitok.modnyi.entity.request.SaveAdsSpends;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.AdsSpendRepository;
import shop.chobitok.modnyi.service.entity.FinanceStats;
import shop.chobitok.modnyi.specification.AdsSpendsSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdsSpendsService {

    private AdsSpendRepository adsSpendRepository;
    private FinanceService financeService;

    public AdsSpendsService(AdsSpendRepository adsSpendRepository, FinanceService financeService) {
        this.adsSpendRepository = adsSpendRepository;
        this.financeService = financeService;
    }

    public List<AdsSpendRec> addOrEditRecord(SaveAdsSpends saveAdsSpends) {
        LocalDate startLocalDate = DateHelper.formDate(saveAdsSpends.getStart());
        LocalDate endLocalDate = DateHelper.formDate(saveAdsSpends.getEnd());
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
/*        Locale locale = Locale.UK;
        int weekOfYearStartDay = start.get(WeekFields.of(locale).weekOfYear());
        int weekOfYearEndDay = start.get(WeekFields.of(locale).weekOfYear());
        if (weekOfYearStartDay != weekOfYearEndDay) {
            throw new ConflictException("Dates are not in the same week");
        }
        if (start.get(WeekFields.of(locale).dayOfWeek()) != 1) {
            throw new ConflictException("Start date is not first day of week");
        }
        if (end.get(WeekFields.of(locale).dayOfWeek()) != 7) {
            throw new ConflictException("End date is not end of week");
        }*/
        return true;
    }

    private List<AdsSpendRec> getAdsSpendRecs(String from, String to) {
        LocalDate fromLocalDate = DateHelper.formDate(from);
        LocalDate toLocalDate = DateHelper.formDate(to);
        return adsSpendRepository.findAll(new AdsSpendsSpecification(fromLocalDate, toLocalDate));
    }

    public FinanceStats getFinanceStats(String from, String to) {
        List<AdsSpendRec> adsSpendRecList = getAdsSpendRecs(from, to);
        EarningsResponse earningsResponse = financeService.getEarnings(from, to);
        FinanceStats financeStats = new FinanceStats();
        Double sum = earningsResponse.getSum();
        Double predictedSum = earningsResponse.getPredictedSum();
        financeStats.setEarnings(earningsResponse.getSum() + earningsResponse.getPredictedSum() - countSpends(adsSpendRecList));
        return new FinanceStats(sum, predictedSum, earningsResponse.getReceivedPercentage(), sum + predictedSum, countSpends(adsSpendRecList),
                sum + predictedSum - countSpends(adsSpendRecList));
    }

    private Double countSpends(List<AdsSpendRec> adsSpendRecs) {
        Double amount = 0d;
        for (AdsSpendRec adsSpendRec : adsSpendRecs) {
            amount += adsSpendRec.getSpendSum();
        }
        return amount;
    }


}
