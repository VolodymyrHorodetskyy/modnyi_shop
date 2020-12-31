package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.google.docs.repository.GoogleDocsRepository;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.util.StringHelper;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Test
    public void sendMail() {
        mailService.sendStatusNotificationEmail("horodetskyyv@gmail.com", Status.ВІДПРАВЛЕНО);
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
    public void twoPairs() {
        List<Ordered> orderedList = orderRepository.findAll();
        List<Ordered> toSave = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoes().size() >= 2) {
                ordered.getOrderedShoes().sort(Comparator.comparing(o -> shoePriceService.getActualShoePrice(o).getPrice()));
                double price = 0d;
                boolean disc = false;
                for (Shoe shoe : ordered.getOrderedShoes()) {
                    if (!disc) {
                        double shoePrice = shoePriceService.getShoePrice(shoe, ordered).getPrice();
                        if (shoePrice != 0d) {
                            price += Math.ceil((shoePrice / 100) * 75);
                        }
                        disc = true;
                    } else {
                        price += shoePriceService.getShoePrice(shoe, ordered).getPrice();
                    }
                }
                if (ordered.getPrice() < price) {
                    ordered.setPrice(Double.valueOf(price));
                    toSave.add(ordered);
                }
            }
        }
        orderRepository.saveAll(toSave);
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
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        //   Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450296540250");
        for (Ordered ordered : orderedList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
            if (trackingEntity.getData() != null && trackingEntity.getData().size() > 0) {
                Data data = trackingEntity.getData().get(0);
                if (!StringUtils.isEmpty(data.getCityRecipient())) {
                    ordered.setCity(data.getCityRecipient());
                    orderRepository.save(ordered);
                }
            }
        }
    }


    @Test
    public void getToRecreate() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (Ordered ordered : orderedList) {
            if (ordered.getUser().getId() == 2l && ordered.getNpAccountId() == 2l && ordered.getUrgent() != null && ordered.getUrgent()) {
                stringBuilder.append(ordered.getTtn()).append(", ").append(ordered.getReturnSumNP()).append(", ")
                        .append(ordered.getCity()).append(", ")
                        .append(ordered.getAddress()).append(", ").append(ordered.getPostComment()).append(", ")
                        .append(ordered.getClient().getPhone()).append(", ").append(ordered.getClient().getName())
                        .append(", ").append(ordered.getClient().getLastName())
                        .append("\n");
                ++count;
            }
        }
        System.out.println(stringBuilder.toString());
        System.out.println(count);
        /*StringBuilder result = new StringBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        Map<LocalDate, List<Ordered>> localDateOrderedMap = new TreeMap<>();
        for (Ordered ordered : orderedList) {
            if (ordered.getUser().getId() == 2l && (ordered.getUrgent() == null || !ordered.getUrgent())
                    && ordered.getNpAccountId() == 2) {
                addOrderToMap(localDateOrderedMap, ordered);
            }
        }
        for (Map.Entry<LocalDate, List<Ordered>> entry : localDateOrderedMap.entrySet()) {
            result.append(entry.getKey().format(timeFormatter)).append("\n\n");
            List<Ordered> ordereds = entry.getValue();
            for (Ordered ordered : ordereds) {
                      result.append(ordered.getTtn()).append(", ").append(ordered.getReturnSumNP()).append(", ")
                        .append(ordered.getCity()).append(", ")
                        .append(ordered.getAddress()).append(", ").append(ordered.getPostComment()).append(", ")
                        .append(ordered.getClient().getPhone()).append(", ").append(ordered.getClient().getName())
                        .append(", ").append(ordered.getClient().getLastName())
                        .append("\n");
            }
            result.append("\n");
        }
        System.out.println(result.toString());
*/
    }


    private boolean addOrderToMap(Map<LocalDate, List<Ordered>> localDateListMap, Ordered ordered) {
        LocalDate date = ordered.getCreatedDate().toLocalDate();
        List<Ordered> orderedList = localDateListMap.get(date);
        if (orderedList == null) {
            orderedList = new ArrayList<>();
            orderedList.add(ordered);
            localDateListMap.put(date, orderedList);
        } else {
            orderedList.add(ordered);
        }
        return true;
    }

    @Test
    public void test123() {
        List<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            List<Ordered> orderedList = orderRepository.findByClientId(client.getId());
            int amount = 0;
            for (Ordered ordered : orderedList) {
                if (ordered.getStatus() == Status.СТВОРЕНО && ordered.getNpAccountId() == 3) {
                    ++amount;
                }
            }
            if (amount > 1) {
                System.out.println(client.getPhone());
            }
        }
    }

    @Autowired
    private GoogleDocsRepository googleDocsRepository;

    @Autowired
    private GoogleDocsService googleDocsService;

    @Autowired
    private OrderService orderService;

    @Test
    public void googleDocs() throws GeneralSecurityException, IOException {
        //   googleDocsRepository.createDocs();
        //    googleDocsService.updateDeliveryFile("Some text");
        canceledOrderReasonService.updateCanceled();
    }

    @Test
    public void make() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusDays(10), false));
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            Ordered ordered = canceledOrderReason.getOrdered();
            if (canceledOrderReason.isManual()) {
                ordered.setStatus(Status.ВІДМОВА);
                orderRepository.save(ordered);
            }
        }
    }

    @Autowired
    private AppOrderRepository appOrderRepository;
  /*
    @Test
    public void take() {
        List<Ordered> orderedList = orderRepository.findByCreatedDateGreaterThan(LocalDateTime.now().minusDays(3));
        for (Ordered ordered : orderedList) {
            if (StringUtils.isEmpty(ordered.getClient().getMail())) {
                AppOrder appOrder = appOrderRepository.findOneByTtn(ordered.getTtn());
                if (appOrder != null && !StringUtils.isEmpty(appOrder.getMail())) {
                    Client client = ordered.getClient();
                    client.setMail(appOrder.getMail());
                    clientRepository.save(client);
                }
            }
        }
    }

  @Autowired
    private DiscountRepository discountRepository;

    @Test
    public void parseShoe() {
        //  System.out.println(npOrderMapper.parseShoe("193 шкіра (ХУТРО), 37 розмір"));
        //  orderService.importOrderFromTTNString("", 1);
        discountRepository.save(new Discount("friday15", 1, 15, false));
        discountRepository.save(new Discount("25 на другу пару", 2, 25, true));
        discountRepository.findAll();
    }

    @Test
    public void discount() {
        Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450308574310");
        Discount discount = discountRepository.findById(13092l).orElse(null);
        Discount discount2 = discountRepository.findById(13093l).orElse(null);

        npOrderMapper.setPriceAndPrepayment(ordered, 100d, discount);
        System.out.println(ordered.getPrice());
        ordered = orderRepository.findOneByAvailableTrueAndTtn("20450308574310");
        npOrderMapper.setPriceAndPrepayment(ordered, 100d, discount2);
        System.out.println(ordered.getPrice());
    }
*/

    @Autowired
    private AppOrderService appOrderService;

    @Test
    public void fixBrokenOrdrers() {
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.НЕ_ЗНАЙДЕНО), LocalDateTime.now().minusDays(5));
        for (Ordered ordered : orderedList) {
            if (StringUtils.isEmpty(ordered.getPostComment())) {
                System.out.println(ordered.getTtn());
            }
        }
        appOrderService.importNotImported();
        System.out.println("new");
        orderedList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.НЕ_ЗНАЙДЕНО), LocalDateTime.now().minusDays(5));
        for (Ordered ordered : orderedList) {
            if (StringUtils.isEmpty(ordered.getPostComment())) {
                System.out.println(ordered.getTtn());
            }
        }
    }

    @Test
    public void setNpAccount123() {
        List<String> splitted = StringHelper.splitTTNString("20450307331920 20450307872606 20450308463749 20450308639118 20450308641402 20450308643382 20450308677044 20450308704380 20450309043366 20450309048053 20450309049479 20450310215830 20450310223821 20450310223680 20450310222282 20450310290620 20450310310776 20450310316603 20450310330854 20450310335254 20450310336871 20450310347376 20450310349030 20450310377357 20450310389586 20450310402492 20450310422424 20450310445300 20450310559303 20450310560343 20450310561669 20450310562091 20450310562665 20450310563124 20450310564835 20450310565240 20450310565708 20450310566443 20450310566642 20450310590465 20450310605576 20450310614584 20450310924770 20450311239706 20450311476323 20450311479073 20450311539356 20450311540812 20450311541822 20450311542632 20450311543995 20450311549125 20450311550659 20450311573200 20450311577523 20450311579020 20450311587627 20450311588618 20450311548599 20450311548864 20450311549281 20450311549529 20450312067176 20450312069747 20450312075147 20450312077106 20450312077844 20450312079830 20450312092568 20450312093271 20450312093852 20450312102402 20450312112891");
        for (String s : splitted) {
            Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn(s);
            if (ordered.getNpAccountId() == 3l) {
                System.out.println(ordered.getTtn());
            } else {
                ordered.setNpAccountId(3l);
                orderRepository.save(ordered);
            }
        }
    }

    @Test
    public void showUrgent() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        StringBuilder stringBuilder = new StringBuilder();
        for (Ordered ordered : orderedList) {
            if (ordered.getUrgent() != null && ordered.getUrgent() && ordered.getNpAccountId() == 3l) {
                stringBuilder.append(ordered.getTtn()).append("\n")
                        .append(ordered.getPostComment()).append("\n\n");
            }
        }
        System.out.println(stringBuilder.toString());
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void setUser() {
        LocalDateTime localDateTime = LocalDateTime.now().withHour(20);
        User user = userRepository.getOne(1l);
        List<Ordered> orderedList = orderRepository.findAllByStatusInAndLastModifiedDateGreaterThan(Arrays.asList(Status.СТВОРЕНО), localDateTime);
        for (Ordered ordered : orderedList) {
            ordered.setUser(user);
        }
        orderRepository.saveAll(orderedList);
    }

    @Test
    public void setRedeliverySum() {
        List<Ordered> orderList = orderRepository.findByNpAccountId(3l);
        ListTrackingEntity entity = postaRepository.getTrackingEntityList(orderList.get(0), LocalDateTime.now().minusDays(5), LocalDateTime.now());
        for (Ordered ordered : orderList) {
            DataForList filteredData = entity.getData().stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ordered.getTtn())).findFirst().orElse(null);
            if (filteredData != null && ordered.getDateCreated() == null) {
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(filteredData.getDateTime()));
                orderRepository.save(ordered);
            }
        }
    }

    @Autowired
    private DiscountRepository discountRepository;

    @Test
    public void setDiscount() {
        Discount discount = discountRepository.getOne(13092l);
        List<Ordered> orderList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.ОТРИМАНО), LocalDateTime.now().minusDays(25));
        for (Ordered ordered : orderList) {
            if (ordered.getReturnSumNP() > 1200 && ordered.getReturnSumNP() < 1500) {
                ordered.setDiscount(discount);
                orderRepository.save(ordered);
            }
        }
    }

    @Test
    @Transactional
    public void addDiscount() {
        Discount discount = new Discount();
        discount.setShoeNumber(1);
        discount.setDiscountPercentage(10);
        discount.setName("Миколай 10");
        discount.setMain(true);
        Discount discountM = discountRepository.getOne(13092l);
        discountRepository.save(discount);
        discountM.setMain(false);
        discountRepository.save(discountM);
    }

    @Autowired
    private CardService cardService;

    @Autowired
    private NpAccountRepository npAccountRepository;

    @Test
    public void setCard() {
/*        Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450311476323");
        Card card = cardService.getOrSaveAndGetCardByName("444111xxxxxx9359");
        ordered.setCard(card);
        orderRepository.save(ordered);*/

        List<Ordered> orderedList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(Arrays.asList(Status.ВІДПРАВЛЕНО, Status.ДОСТАВЛЕНО, Status.ОТРИМАНО), LocalDateTime.now().minusDays(40));
        for (Ordered ordered : orderedList) {
            if (ordered.getCard() == null) {
                TrackingEntity trackingEntity = postaRepository.getTracking(ordered);
                Card card = cardService.getOrSaveAndGetCardByName(trackingEntity.getData().get(0).getCardMaskedNumber());
                ordered.setCard(card);
                orderRepository.save(ordered);
            }
        }

/*        orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.СТВОРЕНО));
        for (Ordered ordered : orderedList) {
            if (ordered.getCard() == null) {
                ListTrackingEntity entity = postaRepository.getTrackingEntityList(ordered, LocalDateTime.now().minusDays(5), LocalDateTime.now());
                List<DataForList> list = entity.getData();
                if (list.size() > 0) {
                    DataForList filteredData = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ordered.getTtn())).findFirst().orElse(null);
                    DataForList.RedeliveryPaymentCard redeliveryPaymentCard = filteredData.getRedeliveryPaymentCard();
                    if (redeliveryPaymentCard != null) {
                        Card card = cardService.getOrSaveAndGetCardByName(redeliveryPaymentCard.getCardMaskedNumber());
                        ordered.setCard(card);
                        orderRepository.save(ordered);
                    }
                }
            }
        }*/
    }

    @Test
    public void getSumByCard() {
        System.out.println(cardService.getSumByCardId(15582l));

    }

    @Test
    public void test5555() {
        BigDecimal bigDecimal = BigDecimal.TEN;
        //      System.out.println(bigDecimal);
        count123(bigDecimal);
        //      System.out.println(bigDecimal);
    }

    public void count123(BigDecimal bigDecimal) {
        bigDecimal.subtract(BigDecimal.ONE);
        bigDecimal = bigDecimal.subtract(BigDecimal.ONE);
        System.out.println(bigDecimal);
    }

}