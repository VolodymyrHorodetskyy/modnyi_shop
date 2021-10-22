package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.repository.NpAccountRepository;

import java.util.List;

@Service
public class NpAccountService {

    private NpAccountRepository npAccountRepository;
    private ParamsService paramsService;

    public NpAccountService(NpAccountRepository npAccountRepository, ParamsService paramsService) {
        this.npAccountRepository = npAccountRepository;
        this.paramsService = paramsService;
    }

    public NpAccount getActual() {
        return npAccountRepository.findById(paramsService.getActualNpAccountId()).orElse(null);
    }

    public NpAccount getById(Long id) {
        NpAccount npAccount = null;
        if (id != null) {
            npAccount = npAccountRepository.findById(id).orElse(null);
        }
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

    public List<NpAccount> getAll(){
        return npAccountRepository.findAll();
    }

}
