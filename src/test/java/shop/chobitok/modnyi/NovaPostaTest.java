package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.AddShoeToOrderRequest;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.util.FileReader;

import javax.mail.internet.AddressException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private GoogleDocsService googleDocsService;

    @Test
    public void googleTest() {
        googleDocsService.updateReturningsFile("TEST");
    }

/*    @Test
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
    }*/

    /*@Test
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


            *//*        if (!StringUtils.isEmpty(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()))) {
                ordered.setCreatedDate(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()));
                ordered.setLastModifiedDate(ShoeUtil.toLocalDateTime(trackingEntity.getData().get(0).getDateCreated()));
                orderRepository.save(ordered);
            } else {
                ordered.getReturnTtn();
            }*//*
        }
    }*/

/*    @Test
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
    }*/

    @Test
    public void regexTest() {
        "дм лаковані".matches(".*дм\\s.*лак.*");
    }


    @Test
    public void urlDecode() {
        try {
            URLDecoder.decode("name=MONOBANK&phone=0637638967&Email=horodetskyyv%40gmail.com&dont_call=yes&payment=%7B%22sys%22%3A%22none%22%2C%22systranid%22%3A%220%22%2C%22orderid%22%3A%221132944846%22%2C%22products%22%3A%5B%22%D0%9F%D0%BE%D0%BB%D1%83%D0%B1%D0%BE%D1%82%D0%B8%D0%BD%D0%BA%D0%B8+-+%D1%82%D1%83%D1%84%D0%BB%D0%B8+Dr+Benetto+1461++%D0%BC%D0%B0%D1%80%D1%81%D0%B0%D0%BB%D0%B0+%28%D0%B4%D0%BC+%D0%BC%D0%B0%D1%80%D1%81%D0%B0%D0%BB%D0%B0%2C+%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80%3A+36%29%3D1399%22%5D%2C%22amount%22%3A%221399%22%7D&COOKIES=+rerf%3DAAAAAF8MZtqsRxa%2BAxbxAg%3D%3D%3B+_ga%3DGA1.2.2089652771.1594648283%3B+tildauid%3D1594648283412.698009%3B+_fbp%3Dfb.1.1594648284529.1970638536%3B+_gid%3DGA1.2.1259694053.1595245789%3B+tildasid%3D1595249035296.774570%3B+_gat%3D1%3B+previousUrl%3Dchobitok.shop%252F&formid=form209247407&formname=Cart", StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

   /* @Test
    public void findDuplicatesClient() {
        List<Client> clients = clientRepository.findAll();
        Set<String> phones = new HashSet<>();
        for (Client client : clients) {
            if (!phones.add(client.getPhone())) {
                List<Ordered> orderedList = orderRepository.findByClientPhone(client.getPhone());
                Client client1 = orderedList.get(0).getClient();
                for (int i = 1; i < orderedList.size(); i++) {
                    Ordered ordered = orderedList.get(i);
                    if (ordered.getClient().equals(client1)) {
                        ordered.setClient(client1);
                        orderRepository.save(ordered);
                    }
                }
                System.out.println(client.toString());
            }
        }
        clients = clientRepository.findAll();
        for (Client client : clients) {
            if (orderRepository.findByClientId(client.getId()).size() == 0) {
                clientRepository.delete(client);
            }
        }
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getClient() == null) {
                TrackingEntity trackingEntity = novaPostaService.getTrackingEntity(null, ordered.getTtn());
                if (trackingEntity.getData().size() > 0) {
                    ordered.setClient(clientService.parseClient(trackingEntity.getData().get(0)));
                    orderRepository.save(ordered);
                }
            }
        }
    }*/

    // @Autowired
    // private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*   @Test
       public void encode() {
           String auth = "vova" + ":" + "123";
           byte[] encodedAuth = Base64.encodeBase64(
                   auth.getBytes(StandardCharsets.ISO_8859_1));
           System.out.println(new String(encodedAuth));
       }
   */
    @Autowired
    private CheckerService checkerService;

    @Test
    public void checkerTest() {
        checkerService.makeAppOrderNewAgain();
    }

    @Autowired
    private MailService mailService;

    @Autowired
    private SentMailRepository sentMailRepository;

    @Test
    public void sendMail() {
        String mailTemplate = "mail_our_discounts_and_updates_1.html";
        List<Client> clients = clientRepository.findByMailNotNull();
        for (Client client : clients) {
            if (!client.getMail().isEmpty() && sentMailRepository.findByClientId(client.getId()) == null) {
                mailService.sendEmail("Модний чобіток. Знижки і нова колекція літнього взуття",
                        FileReader.getHtmlTemplate("mail_our_discounts_and_updates_1.html"), client.getMail());
                sentMailRepository.save(new SentMail(client.getId(), mailTemplate, client.getMail()));
            }
        }
    }

    @Autowired
    private ShoePriceService shoePriceService;
/*
    @Test
    public void setShoePrices() {
        List<Shoe> shoes = shoeRepository.findAll();
        for (Shoe shoe : shoes) {
            shoePriceService.setNewPrice(shoe, LocalDateTime.of(2019, 11, 1, 0, 0), shoe.getPrice(), shoe.getCost());
        }
    }*/

    @Test
    @Transactional
    public void testShoePrices() {
        Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450270368111");
        Shoe shoe = shoeRepository.getOne(2l);
        shoePriceService.getShoePrice(shoe, ordered);

    }

    @Autowired
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    @Autowired
    private CanceledOrderReasonService canceledOrderReasonService;


    @Test
    public void setCanceled() {

     /*   List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.ВІДМОВА));
        for (Ordered ordered : orderedList) {
            if (canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId()) == null) {
                canceledOrderReasonService.createDefaultReasonOnCancel(ordered);
            }
        }*/
        Instant start = Instant.now();

        //  canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toSeconds();
    }

    @Test
    public void setReturnTtnAndStatus() {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(10));
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }


    @Test
    public void getAll() {
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }


    @Test
    public void testSubstring() {
        String s = "ba --- 23132 ---1321 55";
        s.replaceAll("[^0-9]", "");
    }

    @Autowired
    private NPOrderMapper npOrderMapper;

    //set price where 0
    /*@Test
    public void getPriceZero() {
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getPrice() == 0) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(LocalDateTime.now().minusDays(5), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    DataForList filteredData = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ordered.getTtn())).findFirst().orElse(null);
                    if (filteredData != null) {
                        npOrderMapper.setPriceAndPrepayment(ordered, filteredData.getCost());
                        orderRepository.save(ordered);
                    }
                }
            }
        }
   *//*     orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getPrice() == 0) {
                novaPostaService.createOrUpdateOrderFromNP(ordered);
            }
        }*//*
    }
*/
    @Test
    public void getZero() {
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getPrice() == 0 || ordered.getPrice() == null) {
                System.out.println(ordered.getTtn());
            }
        }
    }

    @Test
    public void payedKeepingCheck() {
       /* List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.ДОСТАВЛЕНО));
        for (Ordered ordered : orderedList) {
            novaPostaService.updateDatePayedKeeping(ordered);
        }
        orderRepository.saveAll(orderedList);*/
        checkerService.checkPayedKeepingOrders();
    }

    @Test
    public void tt() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        for (Ordered ordered : orderedList) {
            ordered.setNpAccountId(2l);
            orderRepository.save(ordered);
        }
    }

    @Test
    public void setNpAccount() {
        List<String> splitted = splitTTNString("");
        StringBuilder result = new StringBuilder();
    }

    private List<String> splitTTNString(String ttns) {
        List<String> ttnsList = new ArrayList<>();
        if (ttns != null) {
            String[] ttnsArray = ttns.split("\\s+");
            for (String ttn : ttnsArray) {
                if (!StringUtils.isEmpty(ttn) && isNumeric(ttn) && ttn.length() == 14) {
                    ttnsList.add(ttn);
                }
            }
        }
        return ttnsList;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Test
    public void setAddress() {
        List<Ordered> orderedList = orderRepository.findAll();
        //   Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450296540250");
        for (Ordered ordered : orderedList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
            if (trackingEntity.getData() != null && trackingEntity.getData().size() > 0) {
                Data data = trackingEntity.getData().get(0);
                if (!StringUtils.isEmpty(data.getRecipientAddress())) {
                    ordered.setAddress(data.getRecipientAddress());
                    orderRepository.save(ordered);
                }
            }
        }
    }


    @Test
    public void getToRecreate() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        StringBuilder stringBuilder = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (ordered.getUser().getId() == 2l) {
                stringBuilder.append(ordered.getTtn()).append(", ").append(ordered.getReturnSumNP()).append(", ")
                        .append(ordered.getAddress()).append(", ").append(ordered.getPostComment()).append(", ")
                        .append(ordered.getClient().getPhone()).append(", ").append(ordered.getClient().getName())
                        .append(", ").append(ordered.getClient().getLastName())
                        .append("\n");
            }
        }
        System.out.println(stringBuilder.toString());
    }

    @Autowired
    private DiscountRepository discountRepository;

    @Test
    public void addDiscount() {
        Discount discount = new Discount();
        discount.setDiscountPercentage(10);
        discount.setMain(true);
        discount.setName("Знижка10");
        discount.setShoeNumber(1);
        discountRepository.save(discount);
    }

    @Test
    public void localDateTest() {
        LocalDate.now();
    }

    @Autowired
    private AppOrderRepository appOrderRepository;

    @Test
    public void appOrders() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.withMinute(0);
        localDateTime = localDateTime.withHour(0);
        localDateTime = localDateTime.withSecond(0);
        List<AppOrder> appOrders = appOrderRepository.findByCreatedDateLessThanAndStatusIn(localDateTime, Arrays.asList(AppOrderStatus.В_обробці, AppOrderStatus.Не_Відповідає, AppOrderStatus.Чекаємо_оплату));
        for (AppOrder appOrder : appOrders) {
            appOrder.setStatus(AppOrderStatus.Новий);
        }
        appOrderRepository.saveAll(appOrders);
    }

    @Test
    public void testMarking() {
        postaRepository.getMarking(orderRepository.findOneByAvailableTrueAndTtn("20450335148950"));
    }

    @Test
    public void testException() {
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }

    @Test
    public void setCityAndCityRef() {
        List<Ordered> orderedList = orderRepository.findByCreatedDateGreaterThanAndCityIsNull(LocalDateTime.now().minusMonths(5));
        for (Ordered ordered : orderedList) {
            Ordered fromNP = novaPostaService.createOrUpdateOrderFromNP(ordered.getTtn(), ordered.getNpAccountId(), null);
            ordered.setCity(fromNP.getCity());
            ordered.setCityRefNP(fromNP.getCityRefNP());
            orderRepository.save(ordered);
        }
    }

    @Autowired
    CompanyService companyService;

    @Test
    public void addCompany() {
        companyService.createCompany(new CreateCompanyRequest("Fenci"));
    }

    @Autowired
    private StatusChangeService statusChangeService;

    @Test
    public void addNPAccount() {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 3, 19, 0, 0);
        System.out.println(statusChangeService.getAllFromDateAndNewStatus(localDateTime, Status.ВІДПРАВЛЕНО).size());
    }

    @Autowired
    MarkingRepository markingRepository;

    @Test
    public void getAllPrintedButNotDelivered() {
        List<Marking> markings = markingRepository.findByOrderedStatusAndPrintedTrue(Status.СТВОРЕНО);
        for (Marking marking : markings) {
            System.out.println(marking.getOrdered().getTtn());
        }
    }

    @Test
    public void getAdressChangedOrders() {
        orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.ЗМІНА_АДРЕСУ), LocalDateTime.now().minusDays(50));
    }

    @Test
    public void getReceivedCanceled() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findByLastModifiedDateGreaterThanEqualAndStatus(
                LocalDateTime.now().minusDays(1), Status.ОТРИМАНО);
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (canceledOrderReason.getReason() == CancelReason.БРАК ||
                    canceledOrderReason.getReason() == CancelReason.ПОМИЛКА) {
                System.out.println(canceledOrderReason.getReason() + "\n"
                        + canceledOrderReason.getComment() + "\n"
                        + canceledOrderReason.getReturnTtn()
                        + "\n");
            }
        }
    }

    @Autowired
    private StatisticService statisticService;

    @Test
    public void here() {
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndLastModifiedDateGreaterThan(Arrays.asList(Status.ОТРИМАНО),
                LocalDateTime.now().withHour(12));
        Set<String> stringSet = new HashSet<>();
        List<Ordered> orderedsFiltered = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (stringSet.add(ordered.getTtn()) && ordered.isPayed()) {
                orderedsFiltered.add(ordered);
            }
        }
        //  System.out.println(statisticService.test(orderedsFiltered));
    }

    @Autowired
    CardService cardService;

    @Autowired
    StatusChangeRepository statusChangeRepository;

    @Test
    public void recalculateCard() {
      /*  OrderedSpecification orderedSpecification = new OrderedSpecification();
   //     orderedSpecification.setFrom(LocalDateTime.now().minusDays(25));
        orderedSpecification.setStatuses(Arrays.asList(Status.ВІДПРАВЛЕНО));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            if (ordered.getDatePayedKeepingNP() == null) {
                System.out.println(ordered.getTtn());
            }
        }*/

        List<StatusChangeRecord> statusChangeRecords = statusChangeRepository.findAllByCreatedDateGreaterThanEqualAndNewStatus(LocalDateTime.now().minusDays(30), Status.ВІДПРАВЛЕНО);
        for (StatusChangeRecord statusChangeRecord : statusChangeRecords) {
            List<StatusChangeRecord> statusChangeRecordList = statusChangeRepository.findOneByNewStatusInAndOrderedId(Arrays.asList(Status.ДОСТАВЛЕНО, Status.ОТРИМАНО, Status.ВІДМОВА),
                    statusChangeRecord.getOrdered().getId());
            if ((statusChangeRecordList == null || statusChangeRecordList.size() == 0)
                    && Duration.between(statusChangeRecord.getCreatedDate(), LocalDateTime.now()).toDays() > 4) {
                System.out.println(statusChangeRecord.getOrdered().getTtn());
            }
        }
    }

    @Test
    public void getAllClientsCSV() {
        List<Client> clients = clientRepository.findAll();
        System.out.println(clients.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (Client client : clients) {
            long count = orderRepository.findByClientId(client.getId())
                    .stream().filter(ordered -> ordered.getStatus() == Status.ОТРИМАНО).count();
            stringBuilder.append(client.getMail() == null ? "" : client.getMail()).append(",")
                    .append(client.getPhone()).append(",")
                    .append(client.getName()).append(",")
                    .append(client.getLastName()).append(",")
                    .append(count)
                    .append("\n");
        }
        System.out.println(stringBuilder.toString());
    }

    @Autowired
    private ShoeService shoeService;

/*    @Test
    public void testOrderedShoe() {
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            for (Iterator<Shoe> it = ordered.getOrderedShoes().iterator(); it.hasNext(); ) {
                Shoe shoe = it.next();
                shoeService.addShoeToOrder(new AddShoeToOrderRequest(ordered.getId(), shoe.getId(), ordered.getSize(), null));
            }
        }
    }*/


}
