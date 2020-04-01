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

    public Ordered createOrderFromNP(FromNPToOrderRequest fromNPToOrderRequest) {
        if (StringUtils.isEmpty(fromNPToOrderRequest.getTtn())) {
            throw new ConflictException("Заповніть ТТН");
        }
        if (StringUtils.isEmpty(fromNPToOrderRequest.getPhone())) {
            fromNPToOrderRequest.setPhone(phoneFromProps);
        }
        TrackingEntity trackingEntity = postaRepository.getTracking(createTrackingRequest(fromNPToOrderRequest));
        if (trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            //if status created
            if (data.getStatusCode().equals(3) || ShoeUtil.convertToStatus(data.getStatusCode()) == Status.СТВОРЕНО) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(LocalDateTime.now().minusDays(10), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    return npOrderMapper.toOrdered(entity, fromNPToOrderRequest.getTtn());
                }
            } else {
                return npOrderMapper.toOrdered(trackingEntity);
            }
        }
        return null;
    }

    public List<Ordered> createOrderedFromTTNFile(FromTTNFileRequest request) {
        List<Ordered> orderedList = new ArrayList<>();
        List<String> strings = ShoeUtil.readTXTFile(request.getPath());
        for (String ttn : strings) {
            FromNPToOrderRequest fromNPToOrderRequest = new FromNPToOrderRequest();
            fromNPToOrderRequest.setPhone("+380637638967");
            fromNPToOrderRequest.setTtn(ttn);
            Ordered ordered = createOrderFromNP(fromNPToOrderRequest);
            orderedList.add(ordered);
        }
        return orderedList;
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

    private GetTrackingRequest createTrackingRequest(FromNPToOrderRequest fromNPToOrderRequest) {
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
