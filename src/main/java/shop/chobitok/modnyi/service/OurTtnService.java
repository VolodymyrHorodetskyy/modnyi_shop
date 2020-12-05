package shop.chobitok.modnyi.service;

import org.apache.http.client.utils.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.OurTTN;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.ImportOrdersFromStringRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.mapper.OurTtnMapper;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OurTtnRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public StringResponse receive(ImportOrdersFromStringRequest request) {
        if (request.getNpAccountId() == null) {
            throw new ConflictException("Акаунт не вказано");
        }
        StringBuilder result = new StringBuilder();
        List<String> splitted = splitTTNString(request.getTtns());
        List<String> filtered = new ArrayList<>();
        for (String ttn : splitted) {
            String r = checkIfExistOnImportWithReturnString(ttn);
            if (r == null) {
                filtered.add(ttn);
            } else {
                result.append(r);
            }
        }
        List<OurTTN> ourTTNS = ourTtnMapper.toOurTtn(postaRepository.getTrackingByTtns(request.getNpAccountId(), filtered), request.getNpAccountId());
        for (OurTTN ourTTN : ourTTNS) {
            if (ourTTN.getStatus() == Status.НЕ_ЗНАЙДЕНО) {
                result.append(" не знайдено");
            } else {
                ourTtnRepository.save(ourTTN);
                result.append(ourTTN.getTtn()).append(" збережено");
            }
        }
        return new StringResponse(result.toString());
    }

    public void updateStatusesOurTtns() {
        List<OurTTN> ourTTNS = ourTtnRepository.findAllByStatusNot(Status.ОТРИМАНО);
        Set<OurTTN> toUpdate = new HashSet<>();
        for (OurTTN ourTTN : ourTTNS) {
            if (checkIfExist(ourTTN.getTtn())) {
                ourTTN.setDeleted(true);
                toUpdate.add(ourTTN);
            } else {
                ourTTN.setDeleted(false);
                toUpdate.add(ourTTN);
            }
            TrackingEntity trackingEntity = postaRepository.getTracking(ourTTN.getNpAccountId(), ourTTN.getTtn());
            Data data = trackingEntity.getData().get(0);
            Status newStatus = ShoeUtil.convertToStatus(data.getStatusCode());
            if (newStatus != null && newStatus != ourTTN.getStatus()) {
                ourTTN.setStatus(newStatus);
                if (newStatus == Status.ДОСТАВЛЕНО) {
                    ourTTN.setDatePayedKeeping(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                }
                toUpdate.add(ourTTN);
            }
        }
        ourTtnRepository.saveAll(toUpdate);
    }

    public Page getTtns(int page, int size, boolean showDeletedAndReceived) {
        Page pageObject;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        if (showDeletedAndReceived) {
            pageObject = ourTtnRepository.findAll(pageable);
        } else {
            pageObject = ourTtnRepository.findAllByDeletedFalseAndStatusNot(Status.ОТРИМАНО, pageable);
        }
        return pageObject;
    }


    public String checkIfExistOnImportWithReturnString(String ttn) {
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

    public boolean checkIfExist(String ttn) {
        boolean result = false;
        if (canceledOrderReasonService.getByReturnTtn(ttn) != null) {
            result = true;
        } else if (orderService.findByTTN(ttn) != null) {
            result = true;
        }
        return result;
    }

}
