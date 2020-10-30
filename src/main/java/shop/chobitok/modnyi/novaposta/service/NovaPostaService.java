package shop.chobitok.modnyi.novaposta.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.entity.*;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;

@Service
public class NovaPostaService {

    private NovaPostaRepository postaRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;

    @Value("${novaposta.phoneNumber}")
    private String phoneFromProps;

    public NovaPostaService(NovaPostaRepository postaRepository, NPOrderMapper npOrderMapper, NPHelper npHelper) {
        this.postaRepository = postaRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
    }

    public Ordered createOrUpdateOrderFromNP(Ordered ordered, FromNPToOrderRequest fromNPToOrderRequest) {
        TrackingEntity trackingEntity = postaRepository.getTracking(createTrackingRequest(fromNPToOrderRequest));
        if (trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            //if status created
            if (data.getStatusCode().equals(3) || ShoeUtil.convertToStatus(data.getStatusCode()) == Status.СТВОРЕНО) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(LocalDateTime.now().minusDays(5), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    Ordered ordered1 = npOrderMapper.toOrdered(entity, fromNPToOrderRequest.getTtn());
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

    public Ordered createOrUpdateOrderFromNP(Ordered ordered) {
        return createOrUpdateOrderFromNP(ordered, new FromNPToOrderRequest(ordered.getTtn()));
    }

    public Ordered createOrderFromNP(FromNPToOrderRequest fromNPToOrderRequest) {
        return createOrUpdateOrderFromNP(null, fromNPToOrderRequest);
    }


    public Ordered updateDatePayedKeeping(Ordered ordered) {
        TrackingEntity trackingEntity = postaRepository.getTracking(createTrackingRequest(ordered.getTtn()));
        Data data = trackingEntity.getData().get(0);
        if (data != null) {
            ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDatePayedKeeping()));
        }
        return ordered;
    }

    public String returnCargo(String ttn) {
        CheckPossibilityCreateReturnResponse checkPossibilityCreateReturnResponse = postaRepository.checkPossibilitReturn(ttn);
        if (checkPossibilityCreateReturnResponse.isSuccess()) {
            if (postaRepository.returnCargo(
                    npHelper.createReturnCargoRequest(ttn, checkPossibilityCreateReturnResponse.getData().get(0).getRef()))) {
                return ttn + "  ... заявку на повернення оформлено";
            }
        } else {
            return ttn + "  ...  " + checkPossibilityCreateReturnResponse.getErrors().get(0);
        }
        return ttn + "  ... заявку на повернення неможливо оформити";
    }

    public TrackingEntity getTrackingEntity(String phone, String ttn) {
        if (StringUtils.isEmpty(phone)) {
            phone = phoneFromProps;
        }
        if (!StringUtils.isEmpty(ttn)) {
            return postaRepository.getTracking(createTrackingRequest(new FromNPToOrderRequest(phone, ttn)));
        } else {
            return null;
        }
    }

    public Status getStatus(String ttn) {
        TrackingEntity trackingEntity = getTrackingEntity(null, ttn);
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            return convertToStatus(trackingEntity.getData().get(0).getStatusCode());
        }
        return null;
    }

    private GetTrackingRequest createTrackingRequest(String ttn) {
        return createTrackingRequest(new FromNPToOrderRequest(ttn));
    }

    private GetTrackingRequest createTrackingRequest(FromNPToOrderRequest fromNPToOrderRequest) {
        if (StringUtils.isEmpty(fromNPToOrderRequest.getTtn())) {
            throw new ConflictException("Заповніть ТТН");
        }
        if (StringUtils.isEmpty(fromNPToOrderRequest.getPhone())) {
            fromNPToOrderRequest.setPhone(phoneFromProps);
        }
        GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
        MethodProperties methodProperties = new MethodProperties();
        List<Document> documentList = new ArrayList<>();
        Document document = new Document();
        document.setDocumentNumber(fromNPToOrderRequest.getTtn());
        document.setPhone(fromNPToOrderRequest.getPhone());
        documentList.add(document);
        methodProperties.setDocuments(documentList);
        getTrackingRequest.setMethodProperties(methodProperties);
        return getTrackingRequest;
    }


}
