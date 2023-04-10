package shop.chobitok.modnyi;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.facebook.RestResponseDTO;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.service.entity.NeedToBePayedResponse;
import shop.chobitok.modnyi.service.horoshop.HoroshopService;
import shop.chobitok.modnyi.service.horoshop.mapper.AppOrderHoroshopMapper;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;
import shop.chobitok.modnyi.telegram.ChobitokLeadsBot;
import shop.chobitok.modnyi.util.DateHelper;
import shop.chobitok.modnyi.util.StringHelper;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static shop.chobitok.modnyi.entity.Status.ОТРИМАНО;
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
        orderedSpecification.setFrom(now().minusMonths(2));
        orderedSpecification.setTo(now().minusMonths(1));
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
        List<Notification> notifications = notificationRepository.findByCreatedDateIsGreaterThan(now().minusDays(1));
        notificationRepository.deleteAll(notifications);
    }

    @Test
    public void payedKeepingCheck() {
        checkerService.checkPayedKeepingOrders();
    }

    @Test
    public void showAllDm() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(now().minusDays(20));
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
        orderedSpecification.setFrom(now().minusDays(8));
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
        Pixel pixel = pixelRepository.findById(28l).orElse(null);
        sendTestEvent(pixel, "TEST11129", "https://mchobitok.org/");
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
                ОТРИМАНО, Status.ВІДПРАВЛЕНО, Status.ВІДМОВА));
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
        Pixel pixel = pixelRepository.findById(27l).orElse(null);
        pixel.setPixelAccessToken("EAAQDgRokLEYBAPHXr08KwHw1EUHlnLZAZBZCBo5UYStN9IEPa1BlqlTTzH6fnE72ceYToFnle6xe8cXuxoqiY2nfvv6rihorpGEaoA8SSv4q8mvddUGzZBBY9ZCHEqwsBH0AGHu55H4qO2T1UwDF0uCJ7BAp5UXjCzLbLO7ZAwUZApzM4lObc9LZC87Fgkcxl9kZD");
        pixelRepository.save(pixel);
    }

    @Autowired
    private ShoePriceRepository shoePriceRepository;

    @Test
    public void shoePriceChange() {
        String str = "2022-08-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        List<ShoePrice> shoePrices = shoePriceRepository.findByCreatedDateGreaterThanEqual(now().minusDays(1));
        shoePrices.forEach(shoePrice -> shoePrice.setFromDate(dateTime));
        shoePriceRepository.saveAll(shoePrices);
    }

    @Autowired
    private FinanceService financeService;
    @Autowired
    private OrderedShoeRepository orderedShoeRepository;

    @Test
    public void getFinance() {
        String str = "2022-08-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        List<OrderedShoe> orderedShoeList = orderedShoeRepository.findAllByCreatedDateGreaterThanEqualAndShoeCompanyId(dateTime, 1177l);
        NeedToBePayedResponse needToBePayedResponse = financeService.generateNeedToBePayedResponse(orderedShoeList, 1177l);
        out.println(needToBePayedResponse);
    }

    @Test
    public void addDomains() {
        variantsRepository.save(new Variants("mchobit.com", Domain, 2));
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
                    CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId());
                    if (canceledOrderReason == null) {
                        out.println(ordered.getTtn() + " " + "не знайдено");
                    } else {

                        out.println(ordered.getOrderedShoeList()
                                .stream().map(orderedShoe -> orderedShoe.getShoe().getModelAndColor()
                                        + " " + orderedShoe.getSize())
                                .collect(Collectors.joining(", ")) + " ," +
                                canceledOrderReason.getReturnTtn() + " " + canceledOrderReason.getStatus());
                    }
                });
    }

    @Test
    public void addAppOrder() {
        AppOrder appOrder = new AppOrder();
        appOrder.setDataParsed(true);
        appOrder.setName("Елена Стародубцева");
        appOrder.setStatus(AppOrderStatus.Новий);
        appOrder.setPhone("380955790177");
        appOrder.setAmount(1699d);
        appOrder.setDelivery("Одесса,нп75");
        appOrder.setProducts(asList("Туфлі Dixie лак ≡ 1699\n" +
                "162 лак\n" +
                "Размер: 38"));
        appOrder.setMail("estarodubtseva1@gmail.com");
        appOrderRepository.save(appOrder);
    }

    @Autowired
    private AppOrderService appOrderService;

    @Test
    public void setDomainToAppOrders() {
        List<AppOrder> appOrders = appOrderRepository
                .findByCreatedDateGreaterThanEqualAndDomainIsNull(now().minusDays(7));
        appOrders.forEach(appOrder -> {
            appOrderService.setDomain(appOrder,
                    appOrderService.getValue(appOrderService.splitQuery(appOrder.getNotDecodedInfo()).get("COOKIES")).split(";"));
        });
        appOrderRepository.saveAll(appOrders);
    }

    @Test
    public void changeFinanceCompanyRecord() {
        CompanyFinanceControl companyFinanceControl =
                companyFinanceControlRepository.findById(36l).orElse(null);
        companyFinanceControl.setDescription("Надя\n" +
                " 174 мех37,38,39,40 \n" +
                "205 мех 37,38,39,40 \n" +
                "208 мех 38,39,40 \n" +
                "130 мех36,37,38");
        companyFinanceControlRepository.save(companyFinanceControl);
    }

    @Test
    public void changeShoePrice() {
        String str = "2022-11-30 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        List<Shoe> shoeList = shoeRepository.findByModelContaining("240");
        shoeList.addAll(shoeRepository.findByModelContaining("220"));
        shoeList.addAll(shoeRepository.findByModelContaining("210"));
        shoeList.addAll(shoeRepository.findByModelContaining("230"));
        shoeList.addAll(shoeRepository.findByModelContaining("260"));
        shoeList.forEach(shoe -> {
            ShoePrice shoePrice = shoePriceService.setNewPrice(shoe, dateTime, 2099d, 1250d);
            if (shoePrice != null) {
                out.println(shoePrice.getShoe().getModelAndColor() + " " + shoePrice.getCost() + " " + shoePrice.getPrice());
            } else {
                out.println(shoe.getModelAndColor() + " не змінено");
            }
        });
    }

    @Test
    public void findAllByStatusIsNotFoundAndRemoved() {
        List<OrderedShoe> orderedShoeList = orderedShoeRepository.findAllByShoeCompanyId(1177l);
        orderedShoeList.forEach(orderedShoe -> {
            Ordered ordered = orderRepository.findByOrderedShoeId(orderedShoe.getId());
            if (ordered == null) {
                //     out.println(orderedShoe.getId());
            } else {
                if (ordered.getStatus().equals(Status.НЕ_ЗНАЙДЕНО) || ordered.getStatus().equals(Status.ВИДАЛЕНО)) {
                    out.println(ordered.getTtn());
                    out.println(shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered).getCost() + "\n");
                }
            }
        });
    }

    @Test
    public void countRemoved() {
        String str = "2022-11-01 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        List<Ordered> orderedList = orderRepository.findAllByStatusInAndCreatedDateGreaterThan(
                asList(Status.НЕ_ЗНАЙДЕНО, Status.ВИДАЛЕНО), dateTime);
        out.println(orderedList.size() + " general amount");
    }

    @Test
    public void getAllCharivno() {
   /*     shoeRepository.findByCompanyId(1177l).forEach(
                shoe -> {
                    out.println(shoe.getId()+" "+shoe.getModelAndColor());
                }
        );*/
        AtomicReference<Double> price = new AtomicReference<>(0d);
        orderedShoeRepository.findAllByShoeIdInAndUsedInCoincidenceFalse(asList(17949l,
                17950l,
                17951l,
                17952l,
                17955l, 17956l, 17957l, 17960l, 17966l, 17969l, 17970l
                , 17971l
                , 17976l
        )).forEach(orderedShoe -> {
            Ordered ordered = orderRepository.findByOrderedShoeId(orderedShoe.getId());
            if (!checkIfOrderInStatuses(ordered, asList(
                    Status.ВИДАЛЕНО, Status.НЕ_ЗНАЙДЕНО
            ))) {
                out.println(ordered.getTtn() + " " + orderedShoe.getShoe().getModelAndColor());
                price.updateAndGet(v -> v + shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered).getCost());
            }
        });
        out.println(price);
    }

    public boolean checkIfOrderInStatuses(Ordered ordered, List<Status> statuses) {
        boolean returnValue = false;
        if (ordered == null) {
            returnValue = true;
        } else {
            for (Status status : statuses) {
                if (status == ordered.getStatus()) {
                    returnValue = true;
                    break;
                }
            }
        }
        return returnValue;
    }

    @Autowired
    private StorageService storageService;

    @Test
    @Transactional
    public void getCanceledLastModified() {
        String str = "2022-12-26 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        canceledOrderReasonRepository.findByLastModifiedDateGreaterThanEqualAndStatus(dateTime,
                ОТРИМАНО).forEach(canceledOrderReason -> {
            Ordered ordered = canceledOrderReason.getOrdered();
            String comment = "байка";
            if (ordered.getPostComment().contains("Хут") ||
                    ordered.getPostComment().contains("хут") ||
                    ordered.getPostComment().contains("ХУТ") ||
                    ordered.getPostComment().contains("Мех")) {
                comment = "хутро";
            }
            String finalComment = comment;
            out.println(ordered.getPostComment());
            ordered.getOrderedShoeList().forEach(orderedShoe -> {
                storageService.createStorageRecord(new CreateStorageRequest(orderedShoe.getShoe().getId(),
                        orderedShoe.getSize(), finalComment));
            });
        });
    }

    @Autowired
    private StorageRepository storageRepository;

    @Test
    public void getStorageRecords() {
        //17970 - 260 шкіра, 17870 - 031 лак, 12227 - 192ч шкіра, 189 замш на беж п. - 17965
        //5799 - 192 марсала, 17950 - 230 беж
  /*      storageService.getStorageRecords(17950l, null, null, null, true)
                .forEach(storageRecord -> {
                    out.println(storageRecord.getShoe().getModelAndColor() + " " + storageRecord.getSize() + " " + storageRecord.getComment());
                });*/

    }

    @Test
    @Transactional
    public void getOrderedShoesNotInStorage() {
        String str = "2022-12-26 00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(dateTime);
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        Set<Ordered> ordereds = new HashSet<>();
        orderedList.forEach(ordered -> {
            ordered.getOrderedShoeList().forEach(orderedShoe -> {
                if (!orderedShoe.getShouldNotBePayed()) {
                    ordereds.add(ordered);
                }
            });
        });
        ordereds.forEach(ordered -> out.println(ordered.getTtn()));
    }

    @Test
    @Transactional
    public void countFromFileDelivery() throws URISyntaxException {

        //String fileName = "database.properties";
        String fileName = "txt/delivery 2.txt";

        System.out.println("getResourceAsStream : " + fileName);
        InputStream is = getFileFromResourceAsStream(fileName);
        //   printInputStream(is);

        System.out.println("\ngetResource : " + fileName);
        File file = getFileFromResource(fileName);
        printFile(file);
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    /*
        The resource URL is not working in the JAR
        If we try to access a file that is inside a JAR,
        It throws NoSuchFileException (linux), InvalidPathException (Windows)

        Resource URL Sample: file:java-io.jar!/json/file1.json
     */
    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    // print input stream
    private static void printInputStream(InputStream is) {

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // print a file
    private void printFile(File file) {
        Set<String> ttnSet = new HashSet<>();
        String desc = null;
        List<String> lines;
        Double generalSum = 0d;
        Map<Double, Integer> map = new HashMap<>();
        map.put(1150d, 0);
        map.put(1250d, 0);
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            boolean isTtnLine = true;
            for (String line : lines) {
                if (isTtnLine) {
                    isTtnLine = false;
                    String ttn = StringHelper.removeSpaces(line);
                    boolean notDuplicate = ttnSet.add(ttn);
                    if (notDuplicate) {
                        out.println(ttn);
                        Ordered ordered = orderRepository.findOneByAvailableTrueAndTtn(ttn);
                        if (ordered != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("опис з прогр : ");
                            AtomicReference<Double> sum = new AtomicReference<>(0d);
                            ordered.getOrderedShoeList().forEach(orderedShoe -> {
                                if (orderedShoe.getShoe().getCompany().getId().equals(1177l)) {
                                    stringBuilder.append(orderedShoe.getShoe().getModelAndColor())
                                            .append(" ")
                                            .append(orderedShoe.getSize());
                                    Double cost = shoePriceService.getShoePrice(orderedShoe.getShoe(),
                                            ordered).getCost();
                                    Integer amount = map.get(cost);
                                    ++amount;
                                    map.put(cost, amount);
                                    sum.updateAndGet(v -> v + cost);
                                }
                            });
                            stringBuilder.append(" = ").append(sum.get());
                            desc = stringBuilder.toString();
                            generalSum += sum.get();
                        } else {
                            out.println("не знайдено : " + ttn);
                            desc = null;
                        }
                    } else {
                        out.println("дублікат : " + ttn);
                        desc = null;
                    }
                } else {
                    out.println(desc);
                    out.println("опис = " + line);
                    isTtnLine = true;
                }
            }
            out.println("загальна сума = " + generalSum);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void setUnFor() {
        List<StorageRecord> storageRecords = (List<StorageRecord>) storageService.getStorageRecords(null, null, null, null, true, false);
        storageRecords.forEach(storageRecord -> {
            if (storageRecord.getComment().toLowerCase().contains("хут")) {
                storageRecord.setAvailable(false);
            }
        });
        storageRepository.saveAll(storageRecords);
    }

    @Autowired
    private HoroshopService horoshopService;
    @Autowired
    private AppOrderHoroshopMapper appOrderHoroshopMapper;

    @Test
    public void horoshopTest() throws IOException {
        List<AppOrder> appOrders = appOrderHoroshopMapper.convertToAppOrder(horoshopService.getOrderData(DateHelper.makeDateBeginningOfDay(LocalDateTime.now()), null, null));
        appOrderRepository.saveAll(appOrders);
    }

    @Autowired
    private ChobitokLeadsBot chobitokLeadsBot;

    @Transactional
    @Test
    public void testTelegram() throws JsonProcessingException {
        AppOrder appOrder = appOrderRepository.findByHoroshopOrderId(115l);
        String messageText = String.format("<b>Нове замовлення:</b>\n\n"
                        + "<b>Ім'я:</b> %s\n"
                        + "<b>Телефон:</b> <a href=\"tel:%s\">%s</a>\n"
                        + "<b>Подзвонити:</b> %s\n"
                        + "<b>Продукти:</b> %s\n"
                        + "<b>Дані по доставці:</b> %s",
                appOrder.getName(), appOrder.getPhone(), appOrder.getPhone(),
                appOrder.isDontCall() ? "ні" : "так",
                appOrder.getHoroshopProductsJson(), appOrder.getHoroshopDeliveryDataJson());

        // String appOrderJson = new ObjectMapper().writeValueAsString(appOrder);
        chobitokLeadsBot.sendMessage(messageText, ParseMode.HTML);
    }
} 