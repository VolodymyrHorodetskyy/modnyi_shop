package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NovaPostaTest {

    @Autowired
    private NovaPostaRepository postaRepository;

    @Autowired
    private DtoMapper dtoMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NovaPostaService novaPostaService;

    @Autowired
    private ShoeRepository shoeRepository;

    @Test
    public void testGetTracking() {
        GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
        getTrackingRequest.setApiKey("6c5e8776a25bc714a36eeac4f70b8b37");
        getTrackingRequest.setModelName("TrackingDocument");
        getTrackingRequest.setCalledMethod("getStatusDocuments");
        List<Document> documentList = new ArrayList<>();
        Document document = new Document();
        document.setDocumentNumber("20450207118009");
        documentList.add(document);
        MethodProperties methodProperties = new MethodProperties();
        methodProperties.setDocuments(documentList);
        getTrackingRequest.setMethodProperties(methodProperties);
        postaRepository.getTracking(getTrackingRequest);
    }

    @Test
    public void testListTracking() {
        List<Ordered> ordereds = orderRepository.findAll();
        for (Ordered ordered : ordereds) {
            GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
            getTrackingRequest.setApiKey("6c5e8776a25bc714a36eeac4f70b8b37");
            List<Document> documentList = new ArrayList<>();
            Document document = new Document();
            document.setDocumentNumber(ordered.getTtn());
            document.setPhone("+380637638967");
            documentList.add(document);
            MethodProperties methodProperties = new MethodProperties();
            methodProperties.setDocuments(documentList);
            getTrackingRequest.setMethodProperties(methodProperties);
            TrackingEntity trackingEntity = postaRepository.getTracking(getTrackingRequest);
            LocalDateTime localDateTime = ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated());
            String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            System.out.println("update ordered set created_date = '" + s + "' where ttn = '" + ordered.getTtn() + "';");


            /*        if (!StringUtils.isEmpty(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()))) {
                ordered.setCreatedDate(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()));
                ordered.setLastModifiedDate(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()));
                orderRepository.save(ordered);
            } else {
                ordered.getTtn();
            }*/
        }
    }

    @Test
    public void findBadParsedShoes() {
        List<Ordered> orderedList = orderRepository.findAll();
        int lak = 0;
        for (Ordered ordered : orderedList) {
            Ordered createdOrdered = novaPostaService.createOrderFromNP(new FromNPToOrderRequest(ordered.getTtn()));
            if (createdOrdered == null || createdOrdered.getOrderedShoes() == null || createdOrdered.getOrderedShoes().size() < 1 || createdOrdered.getSize() == null ||
                    createdOrdered.getSize() == 0) {
                System.out.println(ordered.getTtn() + " " + ordered.getPostComment() + "\n");
            }
        }
        System.out.println(lak);
    }

    @Test
    public void regexTest(){
        "дм лаковані".matches(".*дм\\s.*лак.*");

    }

}
