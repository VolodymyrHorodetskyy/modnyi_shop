package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.repository.ParamsRepository;

@Service
public class ParamsService {

    private ParamsRepository paramsRepository;

    public ParamsService(ParamsRepository paramsRepository) {
        this.paramsRepository = paramsRepository;
    }

    public Long getActualNpAccountId() {
        return Long.parseLong(paramsRepository.findByKey("mainNpAccount").getValue());
    }

}
