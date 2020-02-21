package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
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
        return npOrderMapper.toOrdered(postaRepository.getTracking(createTrackingRequest(fromNPToOrderRequest)));
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
