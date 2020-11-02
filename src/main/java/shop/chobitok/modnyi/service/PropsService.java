package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.repository.NpAccountRepository;

@Service
public class PropsService {

    @Value("${novaposta.actualaccount.id}")
    private Long npActualId;
    private NpAccountRepository npAccountRepository;

    public PropsService(NpAccountRepository npAccountRepository) {
        this.npAccountRepository = npAccountRepository;
    }

    public NpAccount getActual() {
        return npAccountRepository.findById(npActualId).orElse(null);
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
