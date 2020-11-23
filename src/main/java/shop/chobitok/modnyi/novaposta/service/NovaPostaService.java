package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.*;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.service.PropsService;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;

@Service
public class NovaPostaService {

    private NovaPostaRepository postaRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;
    private PropsService propsService;
    private OrderRepository orderRepository;


    public NovaPostaService(NovaPostaRepository postaRepository, NPOrderMapper npOrderMapper, NPHelper npHelper, PropsService propsService, OrderRepository orderRepository) {
        this.postaRepository = postaRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
        this.propsService = propsService;
        this.orderRepository = orderRepository;
    }

    public Ordered createOrUpdateOrderFromNP(String ttn) {
        return formOrderedFromNPEntity(null, ttn, postaRepository.getTracking(ttn));
    }

    public Ordered createOrUpdateOrderFromNP(Ordered ordered, String ttn) {
        return formOrderedFromNPEntity(ordered, ttn, postaRepository.getTracking(ordered));
    }

    public Ordered formOrderedFromNPEntity(Ordered ordered, String ttn, TrackingEntity trackingEntity) {
        if (trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            //if status created
            if (data.getStatusCode().equals(3) || ShoeUtil.convertToStatus(data.getStatusCode()) == Status.СТВОРЕНО) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(ordered, LocalDateTime.now().minusDays(5), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    Ordered ordered1 = npOrderMapper.toOrdered(entity, ttn);
                    if (ordered1 != null) {
                        return ordered1;
                    } else {
                        return npOrderMapper.toOrdered(ordered, trackingEntity);
                    }
                }
            } else {
                return npOrderMapper.toOrdered(ordered, trackingEntity);
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
        CheckPossibilityCreateReturnResponse checkPossibilityCreateReturnResponse = postaRepository.checkPossibilitReturn(ordered);
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

    public Status getStatusByTTN(String ttn) {
        return ShoeUtil.convertToStatus(postaRepository.getTracking(ttn).getData().get(0).getStatusCode());
    }




}
