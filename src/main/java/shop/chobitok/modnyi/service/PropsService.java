package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.repository.NpAccountRepository;

@Service
public class PropsService {

    private NpAccountRepository npAccountRepository;
    private ParamsService paramsService;

    public PropsService(NpAccountRepository npAccountRepository, ParamsService paramsService) {
        this.npAccountRepository = npAccountRepository;
        this.paramsService = paramsService;
    }

    public NpAccount getActual() {
        return npAccountRepository.findById(paramsService.getActualNpAccountId()).orElse(null);
    }

    public NpAccount getById(Long id) {
        NpAccount npAccount = npAccountRepository.findById(id).orElse(null);
        if (npAccount == null) {
            return getActual();
        }
        return npAccount;
    }

    public NpAccount getByOrder(Ordered ordered) {
        if (ordered == null) {
            return getActual();
        }
        return getById(ordered.getNpAccountId());
    }

}
