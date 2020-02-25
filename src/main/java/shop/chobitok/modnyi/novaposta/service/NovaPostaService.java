package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Service
public class NovaPostaService {

    private NovaPostaRepository postaRepository;
    private NPOrderMapper npOrderMapper;
    private OrderService orderService;

    public NovaPostaService(NovaPostaRepository postaRepository, NPOrderMapper npOrderMapper, OrderService orderService) {
        this.postaRepository = postaRepository;
        this.npOrderMapper = npOrderMapper;
        this.orderService = orderService;
    }

    public Ordered createOrderFromNP(FromNPToOrderRequest fromNPToOrderRequest) {
        if (StringUtils.isEmpty(fromNPToOrderRequest.getTtn())) {
            throw new ConflictException("Заповніть ТТН");
        }
        if (StringUtils.isEmpty(fromNPToOrderRequest.getPhone())) {
            fromNPToOrderRequest.setPhone("+380637638967");
        }
        return npOrderMapper.toOrdered(postaRepository.getTracking(createTrackingRequest(fromNPToOrderRequest)));
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

    public List<Ordered> createFromTTNListAndSave(FromTTNFileRequest request){
         return orderService.createOrders(createOrderedFromTTNFile(request));
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
