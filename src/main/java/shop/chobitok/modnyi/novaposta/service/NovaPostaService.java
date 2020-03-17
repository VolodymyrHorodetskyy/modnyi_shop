package shop.chobitok.modnyi.novaposta.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
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
    private String phone;

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
            fromNPToOrderRequest.setPhone(phone);
        }
        TrackingEntity trackingEntity = postaRepository.getTracking(createTrackingRequest(fromNPToOrderRequest));
        if (trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            //if status created
            if (ShoeUtil.convertToStatus(data.getStatusCode()) == Status.CREATED) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(LocalDateTime.now().minusDays(10), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    DataForList filteredData = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(fromNPToOrderRequest.getTtn())).findFirst().orElse(null);
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

    public boolean returnCargo(String ttn) {
        return postaRepository.returnCargo(npHelper.createReturnCargoRequest(ttn));
    }

    public boolean returnCargoFromFile(String path) {
        List<String> ttnList = ShoeUtil.readTXTFile(path);
        for (String s : ttnList) {
            if (!returnCargo(s)) {
                throw new ConflictException("Неможливо повернути відправлення :" + s);
            }
        }
        return true;
    }

    public Status getNewStatus(Ordered ordered) {
        return createOrderFromNP(new FromNPToOrderRequest(null, ordered.getTtn())).getStatus();
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
