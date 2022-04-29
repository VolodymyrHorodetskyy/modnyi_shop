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
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;
import shop.chobitok.modnyi.facebook.FacebookApi;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.mapper.DtoMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.out;
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

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

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

    @Autowired
    private ShoePriceService shoePriceService;

    @Test
    @Transactional
    public void testShoePrices() {
        Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn("20450270368111");
        Shoe shoe = shoeRepository.getOne(2L);
        shoePriceService.getShoePrice(shoe, ordered);
    }

    @Autowired
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    @Autowired
    private CanceledOrderReasonService canceledOrderReasonService;

    @Autowired
    private NPOrderMapper npOrderMapper;

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

    @Autowired
    private AppOrderRepository appOrderRepository;

    @Test
    public void testMarking() {
        postaRepository.getMarking(orderRepository.findOneByAvailableTrueAndTtn("20450335148950"));
    }

    @Test
    public void testException() {
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
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
        orderRepository.findAllByStatusInAndCreatedDateGreaterThan(List.of(Status.ЗМІНА_АДРЕСУ), now().minusDays(50));
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
    public void getAllClientsCSV() {
        StringBuilder stringBuilder = new StringBuilder();
        OrderedSpecification specification = new OrderedSpecification();
        specification.setFrom(now().minusDays(120));
        List<Ordered> orderedList = orderRepository.findAll(specification);
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
        out.println(stringBuilder);
    }

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void doJob() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:toread.txt");
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

    @Autowired
    private FacebookApi facebookApi;

    @Autowired
    private FacebookApi2 facebookApi2;

    @Autowired
    private AppOrderToPixelRepository appOrderToPixelRepository;

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

    @Autowired
    private AppOrderToPixelService appOrderToPixelService;

    @Autowired
    private OrderService orderService;

    @Test
    public void checkOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(now().minusDays(100));
        orderedSpecification.setNpAccountId(2L);
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);

        List<Data> dataList = postaRepository.getTrackingByTtns(2L,
                orderedList.stream().map(Ordered::getTtn).collect(Collectors.toList()));
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
        AppOrder appOrder = appOrderRepository.findById(24349L).orElse(null);
        assert appOrder != null;
        appOrder.setCreatedDate(now());
        appOrder.setPixel(pixelRepository.findById(14L).orElse(null));
        facebookApi2.send("TEST50372", appOrder);
    }

    @Test
    public void addVariants() {
        variantsRepository.save(new Variants("телефонія", VariantType.CostsType, 6));
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
        Variants otherSpendVariants = variantsRepository.findById(4L).orElse(null);
        Variants adsSpendVariants = variantsRepository.findById(3L).orElse(null);
        for (SpendRec spendRec : spendRecs) {
            Variants currentVariants;
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
    public void addRoles() {
        User user = userRepository.findById(1L).orElse(null);
        assert user != null;
        user.setRoles(asList(Role.EMPLOYEE, Role.ADMIN));
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    @Test
    public void setAvailable() {
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
    private NotificationRepository notificationRepository;

    @Test
    public void removeNotifications() {
        List<Notification> notifications = notificationRepository.findByCreatedDateIsGreaterThan(LocalDateTime.now().minusDays(1));
        notificationRepository.deleteAll(notifications);
    }

    @Test
    public void addVariantsForOrderSource(){
        Variants variants = new Variants();
        variants.setVariantType(VariantType.Source_of_order);
        variants.setGetting("заявка з сайту (фб, інста)");
        variantsRepository.save(variants);

        Variants variants2 = new Variants();
        variants2.setVariantType(VariantType.Source_of_order);
        variants2.setGetting("переписка в фб");
        variantsRepository.save(variants2);

        Variants variants3 = new Variants();
        variants3.setVariantType(VariantType.Source_of_order);
        variants3.setGetting("переписка в фб (бот)");
        variantsRepository.save(variants3);

        Variants variants4 = new Variants();
        variants4.setVariantType(VariantType.Source_of_order);
        variants4.setGetting("переписка в інста");
        variantsRepository.save(variants4);

        Variants variants5 = new Variants();
        variants5.setVariantType(VariantType.Source_of_order);
        variants5.setGetting("переписка в інста (бот)");
        variantsRepository.save(variants5);

        Variants variants6 = new Variants();
        variants6.setVariantType(VariantType.Source_of_order);
        variants6.setGetting("не визначено");
        variantsRepository.save(variants6);
    }

    @Autowired
    private CompanyFinanceControlService companyFinanceControlService;

    @Autowired
    private CompanyFinanceControlRepository companyFinanceControlRepository;

    @Test
    public void addFirstRecords(){


        CompanyFinanceControl companyFinanceControl = new CompanyFinanceControl();
        companyFinanceControl.setCompany(companyService.getCompany(1175L));
        companyFinanceControl.setDescription("first");
        companyFinanceControl.setOperation(0D);
        companyFinanceControl.setCurrentFinanceState(0D);
        companyFinanceControlRepository.save(companyFinanceControl);
    }
}
