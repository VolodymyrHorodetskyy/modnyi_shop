package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Discount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.CheckPossibilityCreateReturnResponse;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;

@Service
public class NovaPostaService {

    private NovaPostaRepository postaRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;

    public NovaPostaService(NovaPostaRepository postaRepository, NPOrderMapper npOrderMapper, NPHelper npHelper) {
        this.postaRepository = postaRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
    }

    public Ordered createOrUpdateOrderFromNP(String ttn, Long npAccountId, Discount discount) {
        return formOrderedFromNPEntity(null, ttn, postaRepository.getTracking(npAccountId, ttn), discount);
    }

    public Ordered createOrUpdateOrderFromNP(Ordered ordered, String ttn, Discount discount) {
        return formOrderedFromNPEntity(ordered, ttn, postaRepository.getTracking(ordered), discount);
    }

    public Ordered formOrderedFromNPEntity(Ordered ordered, String ttn, TrackingEntity trackingEntity, Discount discount) {
        if (trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            //if status created
            if (data.getStatusCode().equals(3) || ShoeUtil.convertToStatus(data.getStatusCode()) == Status.СТВОРЕНО) {
                Ordered ordered1 = npOrderMapper.toOrdered(
                        postaRepository.getDataForListFromListTrackingEntityInFiveDaysPeriod(ttn, 5,
                                ordered != null ? ordered.getNpAccountId() : null),
                        discount);
                if (ordered1 != null) {
                    return ordered1;
                } else {
                    return npOrderMapper.toOrdered(ordered, trackingEntity, discount);
                }
            } else {
                return npOrderMapper.toOrdered(ordered, trackingEntity, discount);
            }
        }
        return null;
    }

    public Ordered updateDatePayedKeeping(Ordered ordered) {
        TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
        Data data = trackingEntity.getData().get(0);
        if (data != null) {
            ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDatePayedKeeping()));
        }
        return ordered;
    }

    public String returnCargo(Ordered ordered) {
        CheckPossibilityCreateReturnResponse checkPossibilityCreateReturnResponse = postaRepository.checkPossibilityReturn(ordered);
        if (checkPossibilityCreateReturnResponse.isSuccess()) {
            if (postaRepository.returnCargo(
                    npHelper.createReturnCargoRequest(ordered, checkPossibilityCreateReturnResponse.getData().get(0).getRef()))) {
                return ordered.getTtn() + "  ... заявку на повернення оформлено";
            }
        } else {
            return ordered.getTtn() + "  ...  " + checkPossibilityCreateReturnResponse.getErrors().get(0);
        }
        return ordered.getTtn() + "  ... заявку на повернення неможливо оформити";
    }

    public Status getStatus(Ordered ordered) {
        TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            return convertToStatus(trackingEntity.getData().get(0).getStatusCode());
        }
        return null;
    }

    public Status getStatusByTTN(Long npAccountId, String ttn) {
        return ShoeUtil.convertToStatus(postaRepository.getTracking(npAccountId, ttn).getData().get(0).getStatusCode());
    }


}
