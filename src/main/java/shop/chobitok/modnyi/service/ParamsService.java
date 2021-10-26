package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Params;
import shop.chobitok.modnyi.repository.ParamsRepository;

@Service
public class ParamsService {

    private ParamsRepository paramsRepository;
    @Value("${params.dateFromNpAccountSearch}")
    private String dateFromParamName;
    @Value("${params.dateToNpAccountSearch}")
    private String dateToParamName;
    @Value("${params.monthlyReceivingPercentage}")
    private String monthlyReceivingPercentage;

    public ParamsService(ParamsRepository paramsRepository) {
        this.paramsRepository = paramsRepository;
    }

    public Long getActualNpAccountId() {
        return Long.parseLong(paramsRepository.findByClue("mainNpAccount").getGetting());
    }

    public int getMonthlyReceivingPercentage() {
        Params monthlyReceivingPercentageParam =
                paramsRepository.findByClue(monthlyReceivingPercentage);
        if (monthlyReceivingPercentageParam != null) {
            return Integer.parseInt(monthlyReceivingPercentageParam.getGetting());
        }
        return 80;
    }

    public void saveDateFromAndDateToSearchNpAccount(String dateFrom, String dateTo) {
        saveOrChangeParam(dateFromParamName, dateFrom);
        saveOrChangeParam(dateToParamName, dateTo);
    }

    public Params saveOrChangeParam(String key, String value) {
        Params params = paramsRepository.findByClue(key);
        if (params == null) {
            params = new Params(key, value);
            params = paramsRepository.save(params);
        } else if (!params.getGetting().equals(value)) {
            params.setGetting(value);
            params = paramsRepository.save(params);
        }
        return params;
    }

    public Params getParam(String key) {
        return paramsRepository.findByClue(key);
    }
}
