package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.facebook.RestResponseDTO;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static shop.chobitok.modnyi.entity.VariantType.Domain;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("prod")
public class ProdTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    @Autowired
    private NovaPostaRepository novaPostaRepository;

    @Test
    public void getSentAndDelivered() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setStatuses(asList(Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            out.println(ordered.getTtn() + " " + ordered.getStatus() + "\n" + ordered.getPostComment());
        }
    }

    @Test
    public void getReturnsDepartment() {
        CanceledOrderReasonSpecification cors = new CanceledOrderReasonSpecification();
        cors.setStatus(Status.ДОСТАВЛЕНО);
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(cors);
        for (CanceledOrderReason c : canceledOrderReasons) {
            out.println(novaPostaRepository.getTracking(4L, c.getReturnTtn()).getData().get(0).getRecipientAddress());
        }
    }

    @Autowired
    private ShoeRepository shoeRepository;

    @Test
    public void renameShoe() {
        Shoe shoe = shoeRepository.findById(17913L).orElse(null);
        assert shoe != null;
        shoe.setModel("174");
        shoeRepository.save(shoe);
    }

    @Test
    public void setUnAvailableForOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusMonths(2));
        orderedSpecification.setTo(LocalDateTime.now().minusMonths(1));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            ordered.setAvailable(false);
        }
        orderRepository.saveAll(orderedList);
    }

    @Autowired
    private CheckerService checkerService;

    @Test
    public void checkPayedKeepingOrders() {
        checkerService.checkPayedKeepingOrders();
    }

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void removeNotifications() {
        List<Notification> notifications = notificationRepository.findByCreatedDateIsGreaterThan(LocalDateTime.now().minusDays(1));
        notificationRepository.deleteAll(notifications);
    }

    @Test
    public void payedKeepingCheck() {
        checkerService.checkPayedKeepingOrders();
    }

    @Test
    public void showAllDm() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusDays(20));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        orderedList = orderedList.stream().filter(ordered -> (ordered.getPostComment().contains("дм") || ordered.getPostComment().contains("Дм")
                || ordered.getPostComment().contains("ДМ")) && ordered.getPostComment().contains("40")).collect(Collectors.toList());
        for (Ordered ordered : orderedList) {
            out.println(ordered.getTtn() + " " + ordered.getPostComment() + " " + ordered.getStatus() + "\n");
        }
    }

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void changeEmail() {
        Client client = clientRepository.findFirstByMail("berezalesa8@gnail.co.com");
        client.setMail("berezalesa8@gmail.com");
        clientRepository.save(client);
    }

    @Autowired
    private ParamsService paramsService;

    @Test
    public void changeParam() {
        paramsService.saveOrChangeParam("prePaymentSum", "150");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void addRoles() {
        User user2 = userRepository.findById(2L).orElse(null);
        user2.setRoles(asList(Role.EMPLOYEE));
        userRepository.save(user2);
    }

    @Autowired
    private VariantsRepository variantsRepository;

    @Test
    public void addVariantsForOrderSource() {
        Variants variants = new Variants();
        variants.setVariantType(VariantType.Source_of_order);
        variants.setGetting("коммент в фб");
        variantsRepository.save(variants);
    }

    @Test
    public void getVariants() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusDays(8));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        Map<Variants, Integer> map = new HashMap<>();
        for (Ordered ordered : orderedList) {
            Integer i = map.get(ordered.getSourceOfOrder());
            if (i != null) {
                map.put(ordered.getSourceOfOrder(), ++i);
            } else {
                map.put(ordered.getSourceOfOrder(), 1);
            }
        }
        for (Map.Entry<Variants, Integer> entry : map.entrySet()) {
            out.println(entry.getKey().getGetting() + " " + entry.getValue());
        }
    }

    @Autowired
    CompanyService companyService;

    @Test
    public void addCompany() {
        companyService.createCompany(new CreateCompanyRequest("Чарівно"));
    }

    @Autowired
    private PixelRepository pixelRepository;

    @Test
    public void addPixel() {
   /*     Pixel pixel = new Pixel();
        pixel.setPixelId("634494904733632");
        pixel.setPixelAccessToken("EAAQDgRokLEYBAESWxU62EXncPHplbjfsdr8cwVHTdRzcYRlzkTVYk1Llfz7OE4MN1UosLBnoE5KqwjrckYWrKNWXBn6MDcUkKDKwHNPA49aElkZBAXotfLv85SVtZAGw5Qlaf9qZBgD98T8uuT3JdqY1JojHBDclTqd0RRLZCV8KQiMM3VaW6CJe78oenZAMZD");
        pixel.setSendEvents(true);
        pixel.setAccName("soc ulyana pazhoba");
        pixel = pixelRepository.save(pixel);*/
        Pixel pixel = pixelRepository.findById(27l).orElse(null);
        sendTestEvent(pixel, "TEST91057", "https://mchobitok.org/");
    }

    @Autowired
    private AppOrderRepository appOrderRepository;

    @Autowired
    private FacebookApi2 facebookApi2;

    public void sendTestEvent(Pixel pixel, String testCode, String eventSourceUrl) {
        AppOrder appOrder = appOrderRepository.findById(24349L).orElse(null);
        appOrder.setEventSourceUrl(eventSourceUrl);
        assert appOrder != null;
        appOrder.setCreatedDate(now());
        appOrder.setPixel(pixel);
        RestResponseDTO restResponseDTO = facebookApi2.send(testCode, appOrder);
        out.println(restResponseDTO.getHttpStatus() + " " + restResponseDTO.getMessage());
    }

    @Test
    public void sendTestEvent2() {
        AppOrder appOrder = appOrderRepository.findById(24349L).orElse(null);
        assert appOrder != null;
        appOrder.setCreatedDate(now());
        appOrder.setPixel(pixelRepository.findById(24l).orElse(null));
        RestResponseDTO restResponseDTO = facebookApi2.send("TEST35898", appOrder);
        out.println(restResponseDTO.getHttpStatus() + " " + restResponseDTO.getMessage());
    }

    @Autowired
    private ShoePriceService shoePriceService;

    @Test
    @Transactional
    public void companyOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setCompanyId(1177l);
        orderedSpecification.setStatuses(asList(Status.ДОСТАВЛЕНО,
                Status.ОТРИМАНО, Status.ВІДПРАВЛЕНО, Status.ВІДМОВА));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            out.println(ordered.getTtn() + "\n" + ordered.getPostComment() + "" +
                    ordered.getStatus()
                    + "\n");
        }
    }

    @Autowired
    private CompanyFinanceControlService companyFinanceControlService;

    @Autowired
    private CompanyFinanceControlRepository companyFinanceControlRepository;


    @Test
    public void addFirstRecords() {
        CompanyFinanceControl companyFinanceControl = new CompanyFinanceControl();
        companyFinanceControl.setCompany(companyService.getCompany(1175L));
        companyFinanceControl.setDescription("first");
        companyFinanceControl.setOperation(0D);
        companyFinanceControl.setCurrentFinanceState(0D);
        companyFinanceControlRepository.save(companyFinanceControl);
    }

    @Autowired
    private AppOrderToPixelService appOrderToPixelService;

    @Test
    public void sendAppOrderToPixel() {
        appOrderToPixelService.sendAll(0, "2022-05-19 00:00");
    }

    @Test
    public void chnageAccessKey() {
        Pixel pixel = pixelRepository.findById(24l).orElse(null);
        pixel.setPixelAccessToken("EAAQDgRokLEYBAKIfgPHbssJmYLS7mkWn5Jod3porPtJ0QGzhejxTNtibH6ZAqskNcBI8UDXsKmQZAB6Ag5AA8LRd54w51U4arb5QYhZA2HGh9nerYGKtoATxZCTYu5J0ZCg7mLnChF4kbA3W8bZCvu5bZBrmLR1ZCdo2T4NfXn956mwWNRbFX9yoi7exWWPhNZCUZD");
        pixelRepository.save(pixel);
    }

    @Autowired
    private ShoePriceRepository shoePriceRepository;

    @Test
    public void shoePriceChange() {
        String str = "2022-08-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        List<ShoePrice> shoePrices = shoePriceRepository.findByCreatedDateGreaterThanEqual(LocalDateTime.now().minusDays(1));
        shoePrices.forEach(shoePrice -> shoePrice.setFromDate(dateTime));
        shoePriceRepository.saveAll(shoePrices);
    }

    @Test
    public void addDomains() {
        variantsRepository.save(new Variants("mchobitok.com", Domain, 2));
        variantsRepository.save(new Variants("mchobitok.org", Domain, 2));
        variantsRepository.save(new Variants("mchobitok.club", Domain, 2));
    }

    @Test
    public void changePixel() {
        Pixel pixel = pixelRepository.findById(23l).orElse(null);
        AppOrderSpecification appOrderSpecification = new AppOrderSpecification();
        appOrderSpecification.setInfoLike("mchobitok.com");
        appOrderSpecification.setDataValid(true);
        List<AppOrder> appOrders = appOrderRepository.findAll(appOrderSpecification);
        facebookApi2.send("TEST13566", appOrders.get(0));
        /*      appOrders.forEach(appOrder -> {
            appOrder.setPixel(pixel);
            appOrder = appOrderRepository.save(appOrder);
            out.println(facebookApi2.send(appOrder).getHttpStatus());
        });*/
    }

    @Test
    @Transactional
    public void getCanceled() {
        String str = "2022-08-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(dateTime);
        orderedSpecification.setStatus(Status.ВІДМОВА);
        orderedSpecification.setCompanyId(1177l);
        orderRepository.findAll(orderedSpecification)
                .forEach(ordered -> {
                    out.println(ordered.getTtn());
                    ordered.getOrderedShoeList()
                            .forEach(orderedShoe -> out.println(orderedShoe.getShoe().getModelAndColor()));
                    out.println("");
                });
    }

    @Test
    public void addAppOrder(){
        AppOrder appOrder = new AppOrder();
        appOrder.setName("Груник Світлана");
        appOrder.setStatus(AppOrderStatus.Новий);
        appOrder.setPhone("380676081399");
        appOrder.setAmount(1999d);
        appOrder.setDelivery("Брюховичі нова ПОЧТА 1");
        appOrder.setProducts(asList("192ч марсала високі-4\n" +
                "Размер: 36\n" +
                "Внутри: Байка"));
        appOrderRepository.save(appOrder);

    }
}