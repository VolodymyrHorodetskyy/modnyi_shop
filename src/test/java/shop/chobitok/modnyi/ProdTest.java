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
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

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
    public void changeMainNp() {
        paramsService.saveOrChangeParam("mainNpAccount", "1");
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
        Pixel pixel = new Pixel();
        pixel.setPixelId("709663950077249");
        pixel.setPixelAccessToken("EAAG6Ad0MA64BAEONx1g87mjALpwhU9klS4xGxZCY3TbxhF85NAd36qKX3bj4p8jZCnwYByE7Ro6GIRPKNgiq9rOgrSqp0NpJYu270LtS6EGXDAWEGzQ4JRfxS9QO3UkNZCklyvoPZBFiAwbkI3yUC3RXVZC6EBJsfp3vKKjFpNloJQ97CS7H1XhzJJE3mURAZD");
        pixel.setSendEvents(true);
        pixel.setAccName("poli ad pxl 5");
        pixelRepository.save(pixel);
    }

    @Autowired
    private AppOrderRepository appOrderRepository;

    @Autowired
    private FacebookApi2 facebookApi2;

    @Test
    public void sendTestEvent() {
        AppOrder appOrder = appOrderRepository.findById(24349L).orElse(null);
        assert appOrder != null;
        appOrder.setCreatedDate(now());
        appOrder.setPixel(pixelRepository.findById(19L).orElse(null));
        facebookApi2.send("TEST42583", appOrder);
    }

    @Autowired
    private ShoePriceService shoePriceService;

    @Test
    @Transactional
    public void companyOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setCompanyId(1177l);
        orderedSpecification.setStatuses(Arrays.asList(Status.ДОСТАВЛЕНО,
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
        Pixel pixel = pixelRepository.findById(19l).orElse(null);
        pixel.setPixelAccessToken("EAAG6Ad0MA64BANWpZAZAVM32zEt2FTpO1YijCgv5Ijn1coaFBdOD2UW1RdDi7dmH0cbPCRgqASSlZAg0KIuqWBxJwFmi4oZAlGTJ54IhW7wnokB7Ba29ZAXZBtZCdGwC1nEM24SUQJYv8vZAbTOZC32gZB6B4BpmQUmkbwWZA4PZB41eA0XCPVToQQDDEqsgRjMsy4kZD");
        pixelRepository.save(pixel);
    }
}