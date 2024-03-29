package shop.chobitok.modnyi.service;

import com.google.api.client.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.service.entity.ImportResp;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.entity.VariantType.Domain;
import static shop.chobitok.modnyi.util.StringHelper.splitPhonesStringBySemiColonAndValidate;

@Service
public class AppOrderService {

    private final AppOrderRepository appOrderRepository;
    private final OrderService orderService;
    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DiscountService discountService;
    private final AppOrderProcessingRepository appOrderProcessingRepository;
    private final ParamsService paramsService;
    private final UserLoggedInRepository userLoggedInRepository;
    private final ImportService importService;
    private final PixelService pixelService;
    private final VariantsService variantsService;
    private final AppOrderToPixelService appOrderToPixelService;

    @Value("${params.fbpOpenTag}")
    private String fbpOpenTagParamName;
    @Value("${params.fbcOpenTag}")
    private String fbcOpenTagParamName;
    @Value("${params.closeTagForFbcAndFbp}")
    private String closeTagForFbcAndFbpParamName;
    @Value("${params.default.source.apporders}")
    private String defaultVariantIdForOrder;

    public AppOrderService(AppOrderRepository appOrderRepository, OrderService orderService, ClientRepository clientRepository, OrderRepository orderRepository, UserRepository userRepository, DiscountService discountService, AppOrderProcessingRepository appOrderProcessingRepository, ParamsService paramsService, UserLoggedInRepository userLoggedInRepository, ImportService importService, PixelService pixelService, VariantsService variantsService, AppOrderToPixelService appOrderToPixelService) {
        this.appOrderRepository = appOrderRepository;
        this.orderService = orderService;
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.discountService = discountService;
        this.appOrderProcessingRepository = appOrderProcessingRepository;
        this.paramsService = paramsService;
        this.userLoggedInRepository = userLoggedInRepository;
        this.importService = importService;
        this.pixelService = pixelService;
        this.variantsService = variantsService;
        this.appOrderToPixelService = appOrderToPixelService;
    }

    public AppOrder getByHoroshopOrderId(Long horoshopOrderId){
        return appOrderRepository.findByHoroshopOrderId(horoshopOrderId);
    }

    public List<AppOrder> saveAll(List<AppOrder> appOrders){
        return appOrderRepository.saveAll(appOrders);
    }

    public AppOrder catchOrder(String s) {
        AppOrder appOrder = new AppOrder();
        appOrder.setStatus(AppOrderStatus.Новий);
        appOrder.setNotDecodedInfo(s);
        appOrder = appOrderRepository.save(appOrder);
        final AppOrder appOrderForParsing = appOrder;
        CompletableFuture.supplyAsync(() -> {
            try {
                return parseData(appOrderForParsing);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
        return appOrder;
    }

    private AppOrder parseData(AppOrder appOrder) throws UnsupportedEncodingException {
        String decoded = decodeUrl(appOrder.getNotDecodedInfo());
        appOrder.setInfo(decoded);
        appOrderRepository.save(appOrder);
        Map<String, List<String>> splittedUrl = splitQuery(decoded);
        appOrder.setName(getValue(splittedUrl.get("name")));
        appOrder.setPhone(getValue(splittedUrl.get("phone")));
        appOrder.setMail(getValue(splittedUrl.get("Email")));
        appOrder.setDontCall(getValue(splittedUrl.get("dont_call")) != null
                && !getValue(splittedUrl.get("dont_call")).isEmpty());
        appOrder.setDelivery(getValue(splittedUrl.get("delivery")));
        //set products ordered
        String paymentValue = getValue(splittedUrl.get("payment"));
        if (!isEmpty(paymentValue)) {
            JSONObject jsonObject = new JSONObject(paymentValue);
            appOrder.setAmount(jsonObject.getDouble("amount"));
            JSONArray jsonArray = jsonObject.getJSONArray("products");
            List<String> orders = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                orders.add(jsonArray.get(i).toString());
            }
            appOrder.setProducts(orders);
        }
        appOrderRepository.save(appOrder);
        setDataForFB(splittedUrl, decoded, appOrder);
        decoded = decode(decoded, UTF_8.name());
        setBrowserData(decoded, appOrder);
        appOrder.setDataParsed(true);
        return appOrderRepository.save(appOrder);
        // assignAppOrderToUserAndSetShouldBeProcessedTime(appOrder);
    }

    public String getValue(List<String> values) {
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public String decodeUrl(String toDecode) throws UnsupportedEncodingException {
        toDecode = decode(toDecode, UTF_8.name());
        toDecode = remove(toDecode, "\"discountvalue\":\"10%\",");
        toDecode = remove(toDecode, "\"discountvalue\":\"15%\",");
        return decode(toDecode, UTF_8.name());
    }

    private void setDataForFB(Map<String, List<String>> spllitedMap, String decodedInfo, AppOrder appOrder) {
        String cookies = getValue(spllitedMap.get("COOKIES"));
        if (!isEmpty(cookies)) {
            String[] splittedCookies = cookies.split(";");
            setPixelInAppOrder(appOrder,
                    getValue(spllitedMap.get("utm_term")));
            setDomain(appOrder, splittedCookies);
            String fbp = null;
            String fbc = null;
            for (String s : splittedCookies) {
                if (s.contains("_fbp")) {
                    fbp = s.split("=")[1];
                } else if (s.contains("_fbc")) {
                    fbc = s.split("=")[1];
                }
            }
            appOrder.setFbc(fbc);
            appOrder.setFbp(fbp);
            setFbcAndFbpFromWholeInfo(decodedInfo, appOrder);
        }
    }

    private void setFbcAndFbpFromWholeInfo(String decodedInfo, AppOrder appOrder) {
        if (isEmpty(appOrder.getFbc())) {
            String fbc = substringBetween(decodedInfo, paramsService.getParam(fbcOpenTagParamName).getGetting(), paramsService.getParam(closeTagForFbcAndFbpParamName).getGetting());
            appOrder.setFbc(fbc);
        }
        if (isEmpty(appOrder.getFbp())) {
            String fbp = substringBetween(decodedInfo, paramsService.getParam(fbpOpenTagParamName).getGetting(), paramsService.getParam(closeTagForFbcAndFbpParamName).getGetting());
            appOrder.setFbp(fbp);
        }
    }

    private void setBrowserData(String decoded, AppOrder appOrder) {
        String userAgent = null;
        String landingPage = null;
        try {
            userAgent = substringBetween(decoded, "userAgent\":\"", "\"");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            landingPage = substringBetween(decoded, "currentVisitLandingPage\":\"", "\"");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        appOrder.setEventSourceUrl(landingPage);
        appOrder.setClientUserAgent(userAgent);
    }

    public boolean setDomain(AppOrder appOrder, String[] splittedCookies) {
        boolean result = false;
        if (splittedCookies != null && splittedCookies.length > 0) {
            List<Variants> variantsList = variantsService.getByType(Domain);
            for (Variants variants : variantsList) {
                for (String cookie : splittedCookies) {
                    if (!isEmpty(cookie) && cookie.contains(variants.getGetting())) {
                        appOrder.setDomain(variants.getGetting());
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean setPixelInAppOrder(AppOrder appOrder, String pixelString) {
        boolean result = false;
        if (!isEmpty(pixelString)) {
            pixelString = splitByComa(pixelString);
            pixelString = removeAllNonDigits(pixelString);
            Pixel pixel = pixelService.getPixel(pixelString);
            if (pixel != null) {
                appOrder.setPixel(pixel);
                result = true;
            }
        }
        return result;
    }

    private String splitByComa(String toSplit) {
        String[] splitted = toSplit.split(",");
        if (splitted.length > 1) {
            return splitted[0];
        }
        return toSplit;
    }

    private String removeAllNonDigits(String s) {
        return s.replaceAll("\\D", "");
    }

    public Map<String, List<String>> splitQuery(String params) {
        if (Strings.isNullOrEmpty(params)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(params.split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        SimpleImmutableEntry simpleImmutableEntry;
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        assert value != null;
        simpleImmutableEntry = new SimpleImmutableEntry<>(
                key != null ? decode(key, UTF_8) : null,
                value != null ? decode(value, UTF_8) : null
        );
        return simpleImmutableEntry;
    }


    public Map<AppOrderStatus, Set<AppOrder>> getAll(Long id, String phoneAndName, String comment, String fromForNotReady, String fromForReady
            , String userId) {
        List<AppOrder> appOrdersNotReady = appOrderRepository.findAll(
                new AppOrderSpecification(id, phoneAndName, comment, DateHelper.formDateTime(fromForNotReady),
                        Arrays.asList(AppOrderStatus.Новий, AppOrderStatus.Не_Відповідає, AppOrderStatus.Чекаємо_оплату, AppOrderStatus.В_обробці), userId),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        List<AppOrder> appOrdersReady = appOrderRepository.findAll(
                new AppOrderSpecification(id, phoneAndName, comment, DateHelper.formDateTime(fromForReady),
                        Arrays.asList(AppOrderStatus.Передплачено, AppOrderStatus.Повна_оплата, AppOrderStatus.Скасовано), userId),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        List<AppOrder> combinedAppOrders;
        if (!isEmpty(userId)) {
            List<AppOrder> newAppOrders = appOrderRepository.findAll(new AppOrderSpecification(phoneAndName, comment, singletonList(AppOrderStatus.Новий), true));
            combinedAppOrders = Stream.concat(appOrdersNotReady.stream(), appOrdersReady.stream()).collect(toList());
            combinedAppOrders = Stream.concat(newAppOrders.stream(), combinedAppOrders.stream()).collect(toList());
        } else {
            combinedAppOrders = Stream.concat(appOrdersNotReady.stream(), appOrdersReady.stream()).collect(toList());
        }
        combinedAppOrders.sort(Comparator.comparing(AppOrder::getCreatedDate).reversed());
        Map<AppOrderStatus, Set<AppOrder>> appOrderMap = new LinkedHashMap<>();
        for (AppOrder appOrder : combinedAppOrders) {
            Set<AppOrder> appOrders1 = appOrderMap.get(appOrder.getStatus());
            if (appOrders1 == null) {
                appOrders1 = new LinkedHashSet<>();
                appOrders1.add(appOrder);
                appOrderMap.put(appOrder.getStatus(), appOrders1);
            } else {
                appOrders1.add(appOrder);
            }
        }
        return appOrderMap;
    }

    public ChangeAppOrderResponse changeAppOrder(ChangeAppOrderRequest request) {
        AppOrder appOrder = appOrderRepository.findById(request.getId()).orElse(null);
        validateAppOrderChange(request, appOrder);
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        } else {
            throw new ConflictException("User not found");
        }
        appOrder.setUser(user);
        String ttn = request.getTtn();
        String message = null;
        boolean storageCoincidenceFound = false;
        if (!isEmpty(ttn)) {
            ttn = ttn.replaceAll("\\s+", "");
            AppOrder appOrder1 = appOrderRepository.findByTtn(ttn);
            if (appOrder1 != null && !appOrder.getId().equals(appOrder1.getId())) {
                throw new ConflictException("Накладна вже додана в заявку, id = " + appOrder1.getId());
            } else {
                appOrder.setTtn(ttn);
            }
            ImportResp importResp = processAppOrderTtn(ttn, appOrder, request, user);
            message = importResp.getStringResult();
            storageCoincidenceFound = importResp.isCoincidenceFound();
        } else {
            appOrder.setTtn(null);
        }
        changeStatus(appOrder, user, request.getStatus(), request.isRemindTomorrow());
        appOrder.setComment(request.getComment());
        setDataForFb(appOrder, request);
        return new ChangeAppOrderResponse(message, appOrderRepository.save(appOrder), storageCoincidenceFound);
    }

    private void setDataForFb(AppOrder appOrder, ChangeAppOrderRequest request) {
        appOrder.setDataValid(request.isDataValid());
        validateCity(request.getCity());
        appOrder.setCityForFb(request.getCity());
        if (isEmpty(request.getTtn())) {
            splitPhonesStringBySemiColonAndValidate(request.getPhones());
            appOrder.setValidatedPhones(request.getPhones());
            appOrder.setFirstNameForFb(request.getName());
            appOrder.setLastNameForFb(request.getLastName());
        }
        if (request.isDataValid() && isEmpty(appOrder.getValidatedPhones())) {
            throw new ConflictException("Телефон не може бути пустим");
        }
        appOrderToPixelService.save(appOrder);
    }

    private void validateCity(String city) {
        if (!isEmpty(city)) {
            if (!Charset.forName("US-ASCII").newEncoder().canEncode(city)) {
                throw new ConflictException("В назві населеного пункту повинні бути тілька латинські букви");
            } else if (city.matches("[0-9]+")) {
                throw new ConflictException("В назві населеного пункту не повино бути цифр");
            }
        }
    }

    private void validateAppOrderChange(ChangeAppOrderRequest request, AppOrder appOrder) {
        if (appOrder == null) {
            throw new ConflictException("AppOrder not found");
        }
        if (request.getStatus() == AppOrderStatus.Скасовано &&
                request.getCancellationReason() == null) {
            throw new ConflictException("Причина скасування не вказана");
        } else {
            appOrder.setCancellationReason(request.getCancellationReason());
        }
        if ((request.getStatus() == AppOrderStatus.Чекаємо_оплату
                || request.getStatus() == AppOrderStatus.Не_Відповідає)
                && !request.isRemindTomorrow()) {
            if (request.getRemindAt() < 0) {
                throw new ConflictException("Нагадати за не може бути 0 чи відємне число");
            }
            appOrder.setRemindOn(now().plusMinutes(request.getRemindAt()));
        }
    }

    public ImportResp processAppOrderTtn(String ttn, AppOrder appOrder, ChangeAppOrderRequest request,
                                         User user) {
        ImportResp importResp = importService.importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId()),
                variantsService.getById(Long.parseLong(defaultVariantIdForOrder)));
        appOrder.setTtn(ttn);
        String mail = appOrder.getMail();
        Ordered ordered = orderService.findByTTN(ttn);
        if (ordered == null) {
            return null;
        }
        if (!isEmpty(mail)) {
            Client client = ordered.getClient();
            client.setMail(mail);
            clientRepository.save(client);
        }
        if (!isEmpty(request.getComment())) {
            if (isEmpty(ordered.getNotes())) {
                ordered.setNotes(request.getComment());
                orderRepository.save(ordered);
            }
        }
        splitPhonesStringBySemiColonAndValidate(request.getPhones());
        appOrder.setValidatedPhones(request.getPhones());
        ordered.setUser(user);
        setDataForFB(appOrder, ordered);
        orderRepository.save(ordered);
        return importResp;
    }

    private void setDataForFB(AppOrder appOrder, Ordered ordered) {
        Client client = ordered.getClient();
        if (client != null) {
            appOrder.setFirstNameForFb(client.getName());
            appOrder.setLastNameForFb(client.getLastName());
            setPhone(appOrder, client);
        }
    }

    private void setPhone(AppOrder appOrder, Client client) {
        String phone = remove(client.getPhone(), "+");
        if (!isEmpty(phone)) {
            if (isEmpty(appOrder.getValidatedPhones())) {
                appOrder.setValidatedPhones(phone);
            } else if (!appOrder.getValidatedPhones().contains(";") &&
                    !appOrder.getValidatedPhones().equals(phone)) {
                appOrder.setValidatedPhones(appOrder.getValidatedPhones() + ";" + phone);
            } else {
                String[] phones = appOrder.getValidatedPhones().split(";");
                boolean found = false;
                for (String p : phones) {
                    if (p.equals(phone)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    appOrder.setValidatedPhones(appOrder.getValidatedPhones() + ";" + phone);
                }
            }
        }
    }

    public AppOrder changeStatus(AppOrder appOrder, User user, AppOrderStatus newStatus, boolean remindTomorrow) {
        if (appOrder.getStatus() != newStatus) {
            appOrder.setPreviousStatus(appOrder.getStatus());
            AppOrderProcessing appOrderProcessing = new AppOrderProcessing(
                    appOrder, user, appOrder.getStatus(), newStatus, appOrder.getPreviousStatus() == null);
            appOrderProcessing.setRemindOn(appOrder.getRemindOn());
            appOrderProcessing.setRemindTomorrow(remindTomorrow);
            appOrderProcessingRepository.save(appOrderProcessing);
        }
        appOrder.setStatus(newStatus);
        if (newStatus != AppOrderStatus.Скасовано) {
            appOrder.setCancellationReason(null);
        }
        if (newStatus != AppOrderStatus.Не_Відповідає && newStatus != AppOrderStatus.Чекаємо_оплату) {
            appOrder.setRemindOn(null);
        }
        return appOrder;
    }
}
