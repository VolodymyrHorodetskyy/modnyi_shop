package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OurTTN;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.mapper.OurTtnMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.OurTtnRepository;

import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.util.StringHelper.splitTTNString;

@Service
public class OurTtnService {

    private OurTtnMapper ourTtnMapper;
    private NovaPostaRepository postaRepository;
    private OrderService orderService;
    private CanceledOrderReasonService canceledOrderReasonService;
    private OurTtnRepository ourTtnRepository;
    private NovaPostaService novaPostaService;

    public OurTtnService(OurTtnMapper ourTtnMapper, NovaPostaRepository postaRepository, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService, OurTtnRepository ourTtnRepository, NovaPostaService novaPostaService) {
        this.ourTtnMapper = ourTtnMapper;
        this.postaRepository = postaRepository;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.ourTtnRepository = ourTtnRepository;
        this.novaPostaService = novaPostaService;
    }

    public StringResponse receive(String ttns) {
        StringBuilder result = new StringBuilder();
        List<String> splitted = splitTTNString(ttns);
        List<String> filtered = new ArrayList<>();
        for (String ttn : splitted) {
            String r = checkIfExist(ttn);
            if (r == null) {
                filtered.add(ttn);
            } else {
                result.append(r);
            }
        }
        List<OurTTN> ourTTNS = ourTtnMapper.toOurTtn(postaRepository.getTrackingByTtns(filtered));
        for (OurTTN ourTTN : ourTTNS) {
            ourTtnRepository.save(ourTTN);
            result.append(ourTTN.getTtn()).append(" збережено");
        }
        return new StringResponse(result.toString());
    }

    public void updateStatusesOurTtns() {
        List<OurTTN> ourTTNS = ourTtnRepository.findAllByStatusNot(Status.ОТРИМАНО);
        List<OurTTN> toUpdate = new ArrayList<>();
        for (OurTTN ourTTN : ourTTNS) {
            Status newStatus = novaPostaService.getStatusByTTN(ourTTN.getTtn());
            if (newStatus != null && newStatus != ourTTN.getStatus()) {
                ourTTN.setStatus(newStatus);
                toUpdate.add(ourTTN);
            }
        }
        ourTtnRepository.saveAll(toUpdate);
    }

    public String checkIfExist(String ttn) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ttn).append(" ");
        if (canceledOrderReasonService.getByReturnTtn(ttn) != null) {
            stringBuilder.append("існує в поверненнях");
        } else if (orderService.findByTTN(ttn) != null) {
            stringBuilder.append("Існує в замовленнях");
        } else if (ourTtnRepository.findFirstByTtn(ttn) != null) {
            stringBuilder.append("існує в наших ттн");
        } else {
            return null;
        }
        return stringBuilder.toString();
    }

}
