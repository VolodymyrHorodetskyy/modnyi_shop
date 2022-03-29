package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;
import shop.chobitok.modnyi.facebook.FacebookApi;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static shop.chobitok.modnyi.util.DateHelper.makeDateBeginningOfDay;
import static shop.chobitok.modnyi.util.DateHelper.makeDateEndOfDay;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class DevTest {

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
        googleDocsService.forTest("TEST");
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
            decode("name=MONOBANK&phone=0637638967&Email=horodetskyyv%40gmail.com&dont_call=yes&payment=%7B%22sys%22%3A%22none%22%2C%22systranid%22%3A%220%22%2C%22orderid%22%3A%221132944846%22%2C%22products%22%3A%5B%22%D0%9F%D0%BE%D0%BB%D1%83%D0%B1%D0%BE%D1%82%D0%B8%D0%BD%D0%BA%D0%B8+-+%D1%82%D1%83%D1%84%D0%BB%D0%B8+Dr+Benetto+1461++%D0%BC%D0%B0%D1%80%D1%81%D0%B0%D0%BB%D0%B0+%28%D0%B4%D0%BC+%D0%BC%D0%B0%D1%80%D1%81%D0%B0%D0%BB%D0%B0%2C+%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80%3A+36%29%3D1399%22%5D%2C%22amount%22%3A%221399%22%7D&COOKIES=+rerf%3DAAAAAF8MZtqsRxa%2BAxbxAg%3D%3D%3B+_ga%3DGA1.2.2089652771.1594648283%3B+tildauid%3D1594648283412.698009%3B+_fbp%3Dfb.1.1594648284529.1970638536%3B+_gid%3DGA1.2.1259694053.1595245789%3B+tildasid%3D1595249035296.774570%3B+_gat%3D1%3B+previousUrl%3Dchobitok.shop%252F&formid=form209247407&formname=Cart", UTF_8.name());
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
/*        String mailTemplate = "mail_our_discounts_and_updates_1.html";
        List<Client> clients = clientRepository.findByMailNotNull();
        for (Client client : clients) {
            if (!client.getMail().isEmpty() && sentMailRepository.findByClientId(client.getId()) == null) {
                mailService.sendEmail("Модний чобіток. Знижки і нова колекція літнього взуття",
                        FileReader.getHtmlTemplate("mail_our_discounts_and_updates_1.html"), client.getMail());
                sentMailRepository.save(new SentMail(client.getId(), mailTemplate, client.getMail()));
            }
        }*/
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
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(now().minusDays(10));
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
                out.println(ordered.getTtn());
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
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndWithoutTTNFalseAndStatusIn(asList(Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
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
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndWithoutTTNFalseAndStatusIn(asList(Status.СТВОРЕНО));
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
        out.println(stringBuilder.toString());
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
        LocalDateTime localDateTime = now();
        localDateTime = localDateTime.withMinute(0);
        localDateTime = localDateTime.withHour(0);
        localDateTime = localDateTime.withSecond(0);
        List<AppOrder> appOrders = appOrderRepository.findByCreatedDateLessThanAndStatusIn(localDateTime, asList(AppOrderStatus.В_обробці, AppOrderStatus.Не_Відповідає, AppOrderStatus.Чекаємо_оплату));
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
        List<Ordered> orderedList = orderRepository.findByCreatedDateGreaterThanAndCityIsNull(now().minusMonths(5));
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
        out.println(statusChangeService.getAllFromDateAndNewStatus(localDateTime, Status.ВІДПРАВЛЕНО).size());
    }

    @Autowired
    MarkingRepository markingRepository;

    @Test
    public void getAllPrintedButNotDelivered() {
        List<Marking> markings = markingRepository.findByOrderedStatusAndPrintedTrue(Status.СТВОРЕНО);
        for (Marking marking : markings) {
            out.println(marking.getOrdered().getTtn());
        }
    }

    @Test
    public void getAdressChangedOrders() {
        orderRepository.findAllByStatusInAndCreatedDateGreaterThan(asList(Status.ЗМІНА_АДРЕСУ), now().minusDays(50));
    }

    @Test
    public void getReceivedCanceled() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findByLastModifiedDateGreaterThanEqualAndStatus(
                now().minusDays(1), Status.ОТРИМАНО);
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (canceledOrderReason.getReason() == CancelReason.БРАК ||
                    canceledOrderReason.getReason() == CancelReason.ПОМИЛКА) {
                out.println(canceledOrderReason.getReason() + "\n"
                        + canceledOrderReason.getComment() + "\n"
                        + canceledOrderReason.getReturnTtn()
                        + "\n");
            }
        }
    }

    @Autowired
    private StatisticService statisticService;

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

        List<StatusChangeRecord> statusChangeRecords = statusChangeRepository.findAllByCreatedDateGreaterThanEqualAndNewStatus(now().minusDays(30), Status.ВІДПРАВЛЕНО);
        for (StatusChangeRecord statusChangeRecord : statusChangeRecords) {
            List<StatusChangeRecord> statusChangeRecordList = statusChangeRepository.findOneByNewStatusInAndOrderedId(asList(Status.ДОСТАВЛЕНО, Status.ОТРИМАНО, Status.ВІДМОВА),
                    statusChangeRecord.getOrdered().getId());
            if ((statusChangeRecordList == null || statusChangeRecordList.size() == 0)
                    && Duration.between(statusChangeRecord.getCreatedDate(), now()).toDays() > 4) {
                out.println(statusChangeRecord.getOrdered().getTtn());
            }
        }
    }

    @Test
    public void getAllClientsCSV() {
        /*List<Client> clients = clientRepository.findAll();
        System.out.println(clients.size());*/
        StringBuilder stringBuilder = new StringBuilder();
        OrderedSpecification specification = new OrderedSpecification();
        specification.setFrom(now().minusDays(120));
        List<Ordered> orderedList = orderRepository.findAll();
        int withoutClientCount = 0;
        for (Ordered ordered : orderedList) {
            Client client = ordered.getClient();
            if (client != null) {
                long count = orderRepository.findByClientId(client.getId())
                        .stream().filter(o -> o.getStatus() == Status.ОТРИМАНО).count();
                stringBuilder.append(client.getMail() == null ? "" : client.getMail()).append(",")
                        .append(client.getPhone()).append(",")
                        .append(client.getName()).append(",")
                        .append(client.getLastName()).append(",")
                        .append(count)
                        .append("\n");
            } else {
                ++withoutClientCount;
            }
        }
        out.println("Without client = " + withoutClientCount);
        out.println(stringBuilder.toString());
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

    @Test
    public void setOrderedCanceledIfCanceledManually() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findByCreatedDateGreaterThanEqual(now().minusDays(90));
        for (CanceledOrderReason reason : canceledOrderReasons) {
            if (reason.isManual() && reason.getOrdered().getStatus() != Status.ВІДМОВА) {
                //  reason.getOrdered().setStatus(Status.ВІДМОВА);
                // orderRepository.save(reason.getOrdered());
                out.println(reason.getOrdered().getTtn());
            }
        }
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void doJob() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:toread.txt");
        InputStream input = resource.getInputStream();
        File file = resource.getFile();
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                ++count;
                if (count == 1) {
                    stringBuilder.append("[");
                }
                stringBuilder
                        .append("{\"value\":\"")
                        .append(line)
                        .append("\"}");
                if (count == 5) {
                    stringBuilder.append("]").append("\n");
                    count = 0;
                } else {
                    stringBuilder.append(",");
                }
            }
        }
        out.println(stringBuilder);
    }

    @Autowired
    ParamsService paramsService;

    @Test
    public void addParams() {
       /* paramsService.saveOrChangeParam("workingHoursWeekDayFrom", "10");
        paramsService.saveOrChangeParam("workingHoursWeekDayTo", "18");
        paramsService.saveOrChangeParam("workingHoursSaturdayFrom", "10");
        paramsService.saveOrChangeParam("workingHoursSaturdayTo", "17");
        paramsService.saveOrChangeParam("workingHoursSundayFrom", "10");
        paramsService.saveOrChangeParam("workingHoursSundayTo", "16");
        paramsService.saveOrChangeParam("minutesAppOrderShouldBeProcessed", "12");
        paramsService.saveOrChangeParam("firstShouldBeProcessedDateOnNow", "true");*/
        paramsService.saveOrChangeParam("fbpOpenTag", "_fbp=");
        paramsService.saveOrChangeParam("fbcOpenTag", "_fbc=");
        paramsService.saveOrChangeParam("closeTagForFbcAndFbp", ";");
    }

    @Autowired
    private AppOrderService appOrderService;

    @Test
    public void shouldBeProcessedTest() {
        appOrderService.getAllAppOrderAndDateTimeWhenShouldBeProcessed("2021-05-19 00:00");
    }

    @Autowired
    private AppOrderProcessingRepository appOrderProcessingRepository;

    @Test
    @Transactional
    public void test() {
    /*    Discount discount = discountRepository.findById(13093l).orElse(null);
        List<Ordered> orderedList = orderRepository.findByCreatedDateGreaterThan(LocalDateTime.now().minusDays(15));
        for (Ordered ordered :
                orderedList) {
            if (ordered.getOrderedShoeList().size() > 1 && ordered.getDiscount() == null) {
                ordered.setDiscount(discount);
                orderRepository.save(ordered);
            }
        }*/
        appOrderRepository.findByRemindOnIsLessThanEqual(now());
    }

    @Test
    public void test123() {
    /*    AppOrderSpecification appOrderSpecification = new AppOrderSpecification();
        appOrderSpecification.setFromCreatedDate(makeDateBeginningOfDay(now().minusDays(1)));
        appOrderSpecification.setToCreatedDate(makeDateEndOfDay(now().minusDays(1)));
        appOrderRepository.findAll(appOrderSpecification);*/
        AppOrderSpecification appOrderSpecification = new AppOrderSpecification();
        appOrderSpecification.setFromCreatedDate(makeDateBeginningOfDay(now().minusDays(1)));
        appOrderSpecification.setToCreatedDate(makeDateEndOfDay(now().minusDays(1)));
        List<AppOrder> appOrders = appOrderRepository
                .findAll(appOrderSpecification);
    }

    @Autowired
    private FacebookApi facebookApi;

    @Test
    public void appOrderTest() throws UnsupportedEncodingException {
        String s = "name=Володимир&phone=0637638967&paymentsystem=cash&payment={\"orderid\":\"1682210314\",\"products\":[\"Ботинки Челси Milana кожа лаковая (208 лак, Размер: 36, Внутри: Байка)=1699\"],\"amount\":\"1699\"}&COOKIES=_fbp=fb.1.1630946970781.281861911; _ga=GA1.2.1071984855.1630946972; _gcl_au=1.1.1071273065.1634718711; _fbc=fb.1.1635431217940.IwAR3Ee60DgwDJZal6A3E3LDyHK2ryNNQqaktFfodboctLgNI4DDsY1Z88G9o; tildauid=1635608913319.913695; _gid=GA1.2.191167613.1635608913; TILDAUTM=utm_term%3D12345%7C%7C%7C; tildasid=1635933524682.871298; previousUrl=chobitok.co%2F; _gat_gtag_UA_196612521_1=1; biatv-cookie={%22firstVisitAt%22:1630946970%2C%22visitsCount%22:55%2C%22campaignCount%22:9%2C%22currentVisitStartedAt%22:1635933522%2C%22currentVisitLandingPage%22:%22https://chobitok.co/%22%2C%22currentVisitOpenPages%22:3%2C%22location%22:%22https://chobitok.co/%22%2C%22userAgent%22:%22Mozilla/5.0%20(Windows%20NT%2010.0%3B%20Win64%3B%20x64)%20AppleWebKit/537.36%20(KHTML%2C%20like%20Gecko)%20Chrome/95.0.4638.54%20Safari/537.36%22%2C%22language%22:%22en-us%22%2C%22encoding%22:%22utf-8%22%2C%22screenResolution%22:%221536x864%22%2C%22currentVisitUpdatedAt%22:1635933837%2C%22utmDataCurrent%22:{%22utm_source%22:%22l.facebook.com%22%2C%22utm_medium%22:%22referral%22%2C%22utm_campaign%22:%22(referral)%22%2C%22utm_content%22:%22/%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1635431217}%2C%22campaignTime%22:1635431217%2C%22utmDataFirst%22:{%22utm_source%22:%22(direct)%22%2C%22utm_medium%22:%22(none)%22%2C%22utm_campaign%22:%22(direct)%22%2C%22utm_content%22:%22(not%20set)%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1630946970}%2C%22geoipData%22:{%22country%22:%22Ukraine%22%2C%22region%22:%22L'vivs'ka%20Oblast'%22%2C%22city%22:%22Lviv%22%2C%22org%22:%22Kyivstar%20PJSC%22}}; bingc-activity-data={%22numberOfImpressions%22:0%2C%22activeFormSinceLastDisplayed%22:21%2C%22pageviews%22:3%2C%22callWasMade%22:0%2C%22updatedAt%22:1635933850}&formid=form313256838&formname=Cart&utm_term=12345";
        appOrderService.splitQuery(s).get("COOKIES").get(0);
        asList(appOrderService.splitQuery(s).get("COOKIES").get(0).split(";")).contains("chobitok.co");

        //  facebookApi.sendEvent();
    }

    @Autowired
    private FacebookApi2 facebookApi2;

    @Autowired
    private AppOrderToPixelRepository appOrderToPixelRepository;

    @Test
    public void sendEventToFB() throws UnsupportedEncodingException {
        //  AppOrder appOrder = appOrderRepository.findById(23744l).orElse(null);
/*        appOrderService.setBrowserData(URLDecoder.decode(appOrder.getInfo(), UTF_8), appOrder);
        appOrder.setCityForFb("Bilhorod-Dnistrovskyi");
        appOrder.setFirstNameForFb("Елена");
        appOrder.setLastNameForFb("Гришенко");
        appOrder.setValidatedPhones("380934663350");
        appOrderRepository.save(appOrder);
        appOrderToPixelService.save(appOrder);*/


        /*facebookApi2.send(appOrder, createFacebookPurchaseEvent(appOrder.getFbp(),
                appOrder.getFbc(),
                phones, emails, 2598));*/
       /* facebookApi2.send("873803720173320",
                "EAAHdNEEx8IsBAFiDyuZCrWg643zZCL9EDl4tHf4QtljZBNm0NP3vgc7JfSqpL39xqkl4cevCgnxCVb9cYmCKun4WsTHk1bnwTViZA75qjnctuqZBPiCnXsHgpEABRiXQH0ZB5jCUFzXLMtcDB9hVKZAOlRgycDtokGQloh4V9xrZCggAfSstW4vnCZCaPOZBVrowsZD",
                facebookEvent);*/
    }

    @Autowired
    private PixelRepository pixelRepository;

    @Test
    public void addPixel() {
        Pixel pixel = new Pixel();
        pixel.setPixelId("347812123487901");
        pixel.setPixelAccessToken("EABFBSoi1TTkBAHb7HnOrrOQQMjlnZAjyIOItUZBQzvVYjQF7900budwsxTAZATm5mwAsUv83KZCLZBmWpUwz8ePlG9AZClKZC10lQmKFdMgd8bNnxCJtRORXrNnpZCvAP7FoDpCjPOeebhkwFGFFuMcWfWxmvGPhxfftHuCo0xCiJmLyRgvi1Xez");
        pixel.setSendEvents(true);
        pixel.setAccName("w2_a_1");
        pixelRepository.save(pixel);
    }

    @Autowired
    private VariantsRepository variantsRepository;

    @Test
    public void addDomains() {
        variantsRepository.save(new Variants("mchobitok.store", VariantType.Domain, 2));
    }

    @Test
    public void setFB() throws UnsupportedEncodingException {
  /*      AppOrderSpecification appOrderSpecification = new AppOrderSpecification();
        appOrderSpecification.setFromCreatedDate(makeDateBeginningOfDay(now()));
        List<AppOrder> appOrders = appOrderRepository.findAll(appOrderSpecification);
        for (AppOrder appOrder : appOrders) {*/
        AppOrder appOrder = appOrderRepository.findById(23744l).orElse(null);

        String decoded = decode(appOrder.getInfo(), UTF_8.name());
        //   Map<String, List<String>> splittedUrl = appOrderService.splitQuery(decoded);
        // appOrderService.setTtnDataForFB(splittedUrl, appOrder);
        appOrder.setInfo(decoded);
     //   appOrderService.setBrowserData(decoded, appOrder);
        appOrderRepository.save(appOrder);
    }
    //   }

    @Autowired
    private AppOrderToPixelService appOrderToPixelService;

    @Test
    public void changeAppOrder() throws UnsupportedEncodingException {
    /*    AppOrderSpecification appOrderSpecification = new AppOrderSpecification();
        appOrderSpecification.setFromCreatedDate(makeDateBeginningOfDay(LocalDateTime.now()));

        AppOrder appOrder = appOrderRepository.findById(23793l).orElse(null);
        appOrderService.catchOrder(appOrder.getNotDecodedInfo());*/
      /*  OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(makeDateBeginningOfDay(LocalDateTime.now()));
        List<Ordered> orderedList = orderRepository.findAll();*/
     /*   List<AppOrderToPixel> appOrderToPixelList = appOrderToPixelRepository.findAllByCreatedDateGreaterThanEqual(makeDateBeginningOfDay(now()))
                .stream().filter(appOrderToPixel -> appOrderToPixel.isSent()).collect(Collectors.toList());
        for (AppOrderToPixel appOrderToPixel : appOrderToPixelList) {
            if (appOrderToPixel.getAppOrder().getDomain() != null && appOrderToPixel.getAppOrder().getDomain().equals("mchobitok.store")) {
                System.out.println(appOrderToPixel.getAppOrder().getPixel().getAccName());
            }
        }*/
        out.println("ldt " + now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        out.println("ctm " + System.currentTimeMillis());
    }

    @Autowired
    private OrderService orderService;

    @Test
    public void checkOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(now().minusDays(100));
        orderedSpecification.setNpAccountId(2l);
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        List<Ordered> toUpdate = new ArrayList<>();

        List<Data> dataList = postaRepository.getTrackingByTtns(2l,
                orderedList.stream().map(ordered -> ordered.getTtn()).collect(Collectors.toList()));
        for (Ordered ordered : orderedList) {
            Data data = dataList.stream().filter(data1 -> data1.getNumber().equals(ordered.getTtn())).findFirst().orElse(null);
            if (data != null) {
                if (data.getRedeliverySum() != null && !data.getRedeliverySum().equals(ordered.getReturnSumNP())) {
                    out.println(ordered.getTtn() + "\n sum from np = " + data.getRedeliverySum() +
                            "\n sum on ordered = " + ordered.getReturnSumNP() + "\n");
                }
            }
        }

    }

    @Test
    public void addApporders() {
        Pixel pixel = new Pixel();
        pixel.setAccName("wojcek personal");
        pixel.setPixelId("1248837435630375");
        pixel.setPixelAccessToken("EAASaHistAxMBACkZBLlWYoBdVBbOLzZBASyNdpRVqJnWR80lOEPAT05S4Hc5C9c0JCxu33tMfJ7z903OlJV3CejtE4lgqfCkR7gCxY4KbvoIIntvtYAxquG8UTM8tUhPwj5TAuERalXpfCeUByF56CTyaewVsCZB4JMg8NB34N5yqsssotP");
        pixel.setSendEvents(true);
        pixelRepository.save(pixel);
    }

    @Test
    public void sendTestEvent() {
        AppOrder appOrder = appOrderRepository.findById(24349l).orElse(null);
        appOrder.setCreatedDate(now());
        appOrder.setPixel(pixelRepository.findById(14l).orElse(null));
        facebookApi2.send("TEST50372", appOrder);
    }

    @Test
    public void addVariants() {
        variantsRepository.save(new Variants("телефонія", VariantType.CostsType, 6));
    }

    @Test
    public void ordered() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setStatus(Status.СТВОРЕНО);
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
    }

    @Autowired
    private SpendRecRepository spendRecRepository;

    @Autowired
    private CostsService costsService;

    @Autowired
    private VariantsService variantsService;

    @Test
    public void spentToCosts() {
        List<SpendRec> spendRecs = spendRecRepository.findAll();
        Variants otherSpendVariants = variantsRepository.findById(4l).orElse(null);
        Variants adsSpendVariants = variantsRepository.findById(3l).orElse(null);
        for (SpendRec spendRec : spendRecs) {
            Variants currentVariants = null;
            if (spendRec.getSpendType() == SpendType.ADS) {
                currentVariants = adsSpendVariants;
            } else {
                currentVariants = otherSpendVariants;
            }
            costsService.addOrEditRecord(spendRec.getStart(),
                    spendRec.getFinish(), currentVariants, new SaveAdsSpendsRequest(null, null, spendRec.getSpends()
                            , null, spendRec.getComment()));
        }
    }

    @Test
    @Transactional
    public void countSizes() {
        Map<Integer, Integer> integerShoeMap = new HashMap<>();
        integerShoeMap.put(36, 0);
        integerShoeMap.put(37, 0);
        integerShoeMap.put(38, 0);
        integerShoeMap.put(39, 0);
        integerShoeMap.put(40, 0);
        integerShoeMap.put(41, 0);
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(makeDateBeginningOfDay(now().withDayOfMonth(1).withMonth(1)));
        orderedSpecification.setTo(makeDateEndOfDay(now().withDayOfMonth(31).withMonth(1)));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                Integer amount = integerShoeMap.get(orderedShoe.getSize());
                integerShoeMap.put(orderedShoe.getSize(), ++amount);
            }
        }
        for (Map.Entry<Integer, Integer> integerIntegerEntry : integerShoeMap.entrySet()) {
            out.println(integerIntegerEntry.getKey() + " " + integerIntegerEntry.getValue());
        }
    }

    @Test
    @Transactional
    public void checkPayedKeepingOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setStatuses(asList(Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                if (orderedShoe.getShoe().getModel().contains("130")) {
                    out.println(ordered.getTtn() + " " + ordered.getPostComment());
                }
            }
        }
    }

    @Test
    public void getCanceledWithoutReturnTtn() {
        CanceledOrderReasonSpecification specification = new CanceledOrderReasonSpecification();
        //      specification.setManual(false);
        specification.setFromLastModifiedDate(now().minusDays(5));
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(specification);
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (canceledOrderReason.getStatus() == Status.ОТРИМАНО) {
                out.println(canceledOrderReason.getReason() + " " +
                        canceledOrderReason.getOrdered().getPostComment());
            }
        }
    }

    @Test
    public void t() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusYears(1).minusDays(3));
        orderedSpecification.setTo(LocalDateTime.now().minusYears(1).plusDays(45));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        int hutro = 0;
        int bayka = 0;
        for (Ordered ordered : orderedList) {
            String postComment = ordered.getPostComment().toLowerCase();
            if (postComment.contains("хут")
            || postComment.contains("мех") || postComment.contains("мєх")) {
                ++hutro;
            } else {
               // out.println(ordered.getPostComment());
                ++bayka;
            }
        }
        out.println("hutro = " + hutro);
        out.println("bayka = " + bayka);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void addRoles(){
        User user = userRepository.findById(2l).orElse(null);
        user.setRoles(asList(Role.EMPLOYEE));
    //    user.setPassword(passwordEncoder.encode("222"));
        userRepository.save(user);
    }
}
