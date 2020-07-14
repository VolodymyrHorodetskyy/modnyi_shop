package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.NotificationRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.FinanceService;
import shop.chobitok.modnyi.service.MailService;
import shop.chobitok.modnyi.service.StatisticService;
import shop.chobitok.modnyi.util.FileReader;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private FinanceService financeService;

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
    public void regexTest() {
        "дм лаковані".matches(".*дм\\s.*лак.*");
    }

    @Test
    public void testList() {
        Shoe shoe = new Shoe();
        shoe.setPatterns(Arrays.asList("pattern", "2"));
        shoeRepository.save(shoe);
    }

    @Autowired
    private NPOrderMapper npOrderMapper;

    @Test
    public void parse() {
        //Creating a File object for directory
        File directoryPath = new File("C:\\Users\\vhorodetskyi\\Pictures\\Saved Pictures");
        //List of all files and directories
        String contents[] = directoryPath.list();
        System.out.println("List of files and directories in the specified directory:");
        for (int i = 0; i < contents.length; i++) {
            System.out.println(contents[i].substring(5, 9));
        }
    }

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private NPHelper npHelper;

    @Test
    public void dateCreated() {
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered order : orderedList) {
        }
        orderRepository.saveAll(orderedList);
        //ShoeUtil.toLocalDateTime(data.getDateCreated())
    }

    @Autowired
    private CheckerService checkerService;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void checker() {
        checkerService.checkCanceledOrders();
    }


    @Autowired
    private MailService mailService;

    @Test
    public void mailSender(){
        mailService.sendStatusNotificationEmail("horodetskyyv@gmail.com", Status.ВІДПРАВЛЕНО);
    }


}
