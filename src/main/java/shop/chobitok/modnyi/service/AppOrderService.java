package shop.chobitok.modnyi.service;

import com.google.api.client.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.entity.VariantType.Domain;
import static shop.chobitok.modnyi.util.DateHelper.formDateFromOrGetDefault;
import static shop.chobitok.modnyi.util.DateHelper.makeDateBeginningOfDay;

@Service
public class AppOrderService {

    private AppOrderRepository appOrderRepository;
    private OrderService orderService;
    private ClientRepository clientRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private DiscountService discountService;
    private AppOrderProcessingRepository appOrderProcessingRepository;
    private ParamsService paramsService;
    private UserLoggedInRepository userLoggedInRepository;
    private ImportService importService;
    private PixelService pixelService;
    private VariantsService variantsService;
    private AppOrderToPixelService appOrderToPixelService;

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

    public AppOrder catchOrder(String s) throws UnsupportedEncodingException {
        AppOrder appOrder = new AppOrder();
        String decoded = URLDecoder.decode(s, UTF_8.name());
        appOrder.setInfo(decoded);
        Map<String, List<String>> splittedUrl = splitQuery(decoded);
        appOrder.setName(getValue(splittedUrl.get("name")));
        appOrder.setPhone(getValue(splittedUrl.get("phone")));
        appOrder.setMail(getValue(splittedUrl.get("Email")));
        appOrder.setDontCall(getValue(splittedUrl.get("dont_call")) != null
                && !getValue(splittedUrl.get("dont_call")).isEmpty());
        appOrder.setDelivery(getValue(splittedUrl.get("delivery")));
        //set products ordered
        JSONObject jsonObject = new JSONObject(getValue(splittedUrl.get("payment")));
        appOrder.setAmount(jsonObject.getDouble("amount"));
        JSONArray jsonArray = jsonObject.getJSONArray("products");
        List<String> orders = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            orders.add(jsonArray.get(i).toString());
        }
        appOrder.setProducts(orders);
        appOrder.setStatus(AppOrderStatus.Новий);
        appOrderRepository.save(appOrder);
        setFBData(splittedUrl, appOrder);
        setBrowserData(decoded, appOrder);
        appOrder = appOrderRepository.save(appOrder);
        // assignAppOrderToUserAndSetShouldBeProcessedTime(appOrder);
        return appOrder;
    }

    private String getValue(List<String> values) {
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public void setFBData(Map<String, List<String>> spllitedMap, AppOrder appOrder) {
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
        }
    }

    public void setBrowserData(String decoded, AppOrder appOrder) {
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

    private boolean setDomain(AppOrder appOrder, String[] splittedCookies) {
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
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        assert value != null;
        simpleImmutableEntry = new SimpleImmutableEntry<>(
                URLDecoder.decode(key, UTF_8),
                URLDecoder.decode(value, UTF_8)
        );
        return simpleImmutableEntry;
    }

    public AppOrder findFirstShouldBeProcessedAppOrderByUserId(Long userId) {
        AppOrder appOrder = appOrderRepository.findFirstByStatusInAndPreviousStatusIsNullAndUserIdOrderByDateAppOrderShouldBeProcessedDesc(
                singletonList(AppOrderStatus.Новий), userId);
        if (appOrder == null) {
            appOrder = appOrderRepository.findFirstByStatusInAndUserIdOrderByCreatedDateDesc(
                    singletonList(AppOrderStatus.Новий), userId);
        }
        return appOrder;
    }

    public void assignAppOrderToUserAndSetShouldBeProcessedTime(AppOrder appOrder) {
        AppOrder lastAppOrder = getLastAppOrder();
        List<UserLoggedIn> usersLoggedIn = userLoggedInRepository.findAllByActiveTrueAndCreatedDateGreaterThanEqual(
                DateHelper.formLocalDateTimeStartOfTheDay(now()));
        User userToAssign = usersLoggedIn.stream().filter(userLoggedIn -> userLoggedIn.isActive() &&
                !userLoggedIn.getUser().getId().equals(lastAppOrder.getUser())).map(UserLoggedIn::getUser)
                .findFirst().orElse(usersLoggedIn.size() > 0 ? usersLoggedIn.get(0).getUser() : null);
        if (userToAssign != null) {
            int endOfDayWorkingHour = getEndOfWorkingDayHour(now().getDayOfWeek());
            int minutesAppOrderShouldBeProcessed =
                    Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
            LocalDateTime nextShouldBeProcessedLocalDateTime = getNextShouldBeProcessedLocalDateTime(now(), endOfDayWorkingHour, minutesAppOrderShouldBeProcessed);
            appOrder.setDateAppOrderShouldBeProcessed(nextShouldBeProcessedLocalDateTime);
            appOrder.setUser(userToAssign);
            appOrderRepository.save(appOrder);
        }
    }

    public void setShouldBeProcessedAppOrderDateAndAssignToUser(List<User> users) {
/*        if (users.size() > 0) {
            int getUserIndex = 0;
            List<AppOrder> appOrders = appOrderRepository.findByStatusInOrderByCreatedDateDesc(
                    Arrays.asList(AppOrderStatus.Новий));
            int endOfDayWorkingHour = getEndOfWorkingDayHour(now().getDayOfWeek());
            int minutesAppOrderShouldBeProcessed =
                    Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
            LocalDateTime shouldBeProcessedDate = findFirstAvailableLocalDateTimeForShouldBeProcessedByUserId(users.get(getUserIndex).getId(),
                    endOfDayWorkingHour, minutesAppOrderShouldBeProcessed);
            for (AppOrder appOrder : appOrders) {
                appOrder.setUser(users.get(getUserIndex));
                appOrder.setDateAppOrderShouldBeProcessed(shouldBeProcessedDate);
                getUserIndex += 1;
                if (getUserIndex + 1 > users.size()) {
                    shouldBeProcessedDate = getNextShouldBeProcessedLocalDateTime(shouldBeProcessedDate,
                            endOfDayWorkingHour,
                            minutesAppOrderShouldBeProcessed);
                    getUserIndex = 0;
                }
            }
            appOrderRepository.saveAll(appOrders);
        }*/
        int endOfDayWorkingHour = getEndOfWorkingDayHour(now().getDayOfWeek());
        int minutesAppOrderShouldBeProcessed =
                Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
        List<AppOrder> appOrders = appOrderRepository.findByStatusInOrderByCreatedDateDesc(
                singletonList(AppOrderStatus.Новий));
        Map<User, LocalDateTime> userLocalDateTimeMap = new HashMap<>();
        Iterator<AppOrder> appOrderIterator = appOrders.iterator();
        while (appOrderIterator.hasNext()) {
            for (User user : users) {
                LocalDateTime lastDateTimeForUser = userLocalDateTimeMap.get(user);
                if (lastDateTimeForUser == null) {
                    lastDateTimeForUser = getNextShouldBeProcessedLocalDateTime(now(), endOfDayWorkingHour,
                            minutesAppOrderShouldBeProcessed);
                } else {
                    lastDateTimeForUser = getNextShouldBeProcessedLocalDateTime(lastDateTimeForUser, endOfDayWorkingHour,
                            minutesAppOrderShouldBeProcessed);
                }
                userLocalDateTimeMap.put(user, lastDateTimeForUser);
                AppOrder appOrder = appOrderIterator.next();
                if (appOrder.getPreviousStatus() == null) {
                }
            }
        }
    }

    private LocalDateTime findFirstAvailableLocalDateTimeForShouldBeProcessedByUserId(Long userId,
                                                                                      int endOfWorkingDay,
                                                                                      int minutesAppOrderShouldBeProcessed) {
        LocalDateTime availableShouldBeProcessedDateTime;
        boolean firstShouldBeProcessedOnNow = Boolean.parseBoolean(paramsService.getParam("firstShouldBeProcessedDateOnNow").getGetting());
        AppOrder lastShouldBeProcessedAppOrderByUser = appOrderRepository.findFirstByDateAppOrderShouldBeProcessedGreaterThanEqualAndUserId(makeDateBeginningOfDay(now()), userId);
        if (lastShouldBeProcessedAppOrderByUser == null) {
            if (firstShouldBeProcessedOnNow) {
                availableShouldBeProcessedDateTime = now();
            } else {
                int startOfDayWorkingHour = getStartOfWorkingDayHour(now().getDayOfWeek());
                availableShouldBeProcessedDateTime = now().withHour(startOfDayWorkingHour).withMinute(0).withSecond(0);
            }
        } else {
            availableShouldBeProcessedDateTime = getNextShouldBeProcessedLocalDateTime(lastShouldBeProcessedAppOrderByUser.getDateAppOrderShouldBeProcessed(),
                    endOfWorkingDay, minutesAppOrderShouldBeProcessed);
        }
        return availableShouldBeProcessedDateTime;
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
        if (!isEmpty(ttn)) {
            ttn = ttn.replaceAll("\\s+", "");
            AppOrder appOrder1 = appOrderRepository.findByTtn(ttn);
            if (appOrder1 != null && !appOrder.getId().equals(appOrder1.getId())) {
                throw new ConflictException("Накладна вже додана в заявку, id = " + appOrder1.getId());
            } else {
                appOrder.setTtn(ttn);
            }
            processAppOrderTtn(ttn, appOrder, request, user);
        }
        changeStatus(appOrder, user, request.getStatus(), request.isRemindTomorrow());
        appOrder.setComment(request.getComment());
        return new ChangeAppOrderResponse(message, appOrderRepository.save(appOrder));
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

    public String processAppOrderTtn(String ttn, AppOrder appOrder, ChangeAppOrderRequest request,
                                     User user) {
        String message;
        message = importService.importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId()));
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
        ordered.setUser(user);
        orderRepository.save(ordered);
        setFBData(appOrder, ordered);
        return message;
    }

    private void setFBData(AppOrder appOrder, Ordered ordered) {
        Client client = ordered.getClient();
        if (client != null) {
            appOrder.setFirstNameForFb(client.getName());
            appOrder.setLastNameForFb(client.getLastName());
            setPhone(appOrder, client);
        }
    }

    private void setPhone(AppOrder appOrder, Client client) {
        String phone = client.getPhone();
        if (!isEmpty(phone)) {
            if (isEmpty(appOrder.getValidatedPhones())) {
                appOrder.setPhone(phone);
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
        // userEfficiencyService.determineEfficiency(appOrder, user);
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
        if ((newStatus == AppOrderStatus.Повна_оплата ||
                newStatus == AppOrderStatus.Передплачено ||
                newStatus == AppOrderStatus.Чекаємо_оплату
                || (newStatus == AppOrderStatus.Скасовано && appOrder.getCancellationReason() == AppOrderCancellationReason.НЕ_ПІДХОДИТЬ_ПЕРЕДПЛАТА)
                || (newStatus == AppOrderStatus.Скасовано && appOrder.getCancellationReason() == AppOrderCancellationReason.НЕ_АКТУАЛЬНО))
                && appOrder.getPixel() != null && appOrder.getPixel().isSendEvents()) {
            appOrderToPixelService.save(appOrder);
        }
        return appOrder;
    }

    public String importNotImported() {
        StringBuilder result = new StringBuilder();
        List<AppOrder> appOrders = appOrderRepository.findByTtnIsNotNullAndLastModifiedDateIsGreaterThan(now().minusDays(3));
        for (AppOrder appOrder : appOrders) {
            if (!isEmpty(appOrder.getTtn()) && orderRepository.findOneByAvailableTrueAndTtn(appOrder.getTtn()) == null) {
                result.append(importService.importOrderFromTTNString(appOrder.getTtn(), appOrder.getUser().getId(), null));
            }
        }
        return result.toString();
    }

    public Map<AppOrder, LocalDateTime> getAllAppOrderAndDateTimeWhenShouldBeProcessed(String from) {
        int minutesAppOrderShouldBeProcessed =
                Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
        LocalDateTime fromLocalDateTime = formDateFromOrGetDefault(from);
        List<AppOrder> appOrders = appOrderRepository.findByCreatedDateGreaterThanEqualOrderByCreatedDateDesc(fromLocalDateTime);
        Map<AppOrder, LocalDateTime> appOrderDateTimeMap = new LinkedHashMap<>();
        LocalDateTime lastShouldBeProcessedDate = null;
        for (AppOrder appOrder : appOrders) {
            LocalDateTime appOrderCreatedDate = appOrder.getCreatedDate();
            int startOfDayWorkingHour = getStartOfWorkingDayHour(appOrderCreatedDate.getDayOfWeek());
            int endOfDayWorkingHour = getEndOfWorkingDayHour(appOrderCreatedDate.getDayOfWeek());
            if (lastShouldBeProcessedDate == null) {
                lastShouldBeProcessedDate = appOrderCreatedDate.withMinute(0)
                        .withSecond(0)
                        .withHour(startOfDayWorkingHour);
            } else {
                int newMinutes = lastShouldBeProcessedDate.getMinute() + minutesAppOrderShouldBeProcessed;
                if (newMinutes >= 60) {
                    int newHour = lastShouldBeProcessedDate.getHour() + 1;
                    if (newHour > endOfDayWorkingHour) {
                        lastShouldBeProcessedDate.withDayOfYear(lastShouldBeProcessedDate.getDayOfYear() + 1);
                        lastShouldBeProcessedDate.withSecond(0).withMinute(0)
                                .withHour(getStartOfWorkingDayHour(lastShouldBeProcessedDate.getDayOfWeek()));
                    } else {
                        lastShouldBeProcessedDate = lastShouldBeProcessedDate.withMinute(newMinutes - 60)
                                .withHour(newHour);
                    }
                } else {
                    lastShouldBeProcessedDate =
                            lastShouldBeProcessedDate.withMinute(newMinutes);
                }

            }
            if (lastShouldBeProcessedDate.getHour() <= endOfDayWorkingHour
                    && lastShouldBeProcessedDate.getHour() >= startOfDayWorkingHour) {
                appOrderDateTimeMap.put(appOrder, lastShouldBeProcessedDate);
            }
        }
        return appOrderDateTimeMap;
    }

    public int getStartOfWorkingDayHour(DayOfWeek dayOfWeek) {
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return Integer.valueOf(paramsService.getParam("workingHoursSaturdayFrom").getGetting()).intValue();
        } else if (dayOfWeek == DayOfWeek.SUNDAY) {
            return Integer.valueOf(paramsService.getParam("workingHoursSundayFrom").getGetting()).intValue();
        } else {
            return Integer.valueOf(paramsService.getParam("workingHoursWeekDayFrom").getGetting()).intValue();
        }
    }

    public int getEndOfWorkingDayHour(DayOfWeek dayOfWeek) {
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return Integer.valueOf(paramsService.getParam("workingHoursSaturdayTo").getGetting()).intValue();
        } else if (dayOfWeek == DayOfWeek.SUNDAY) {
            return Integer.valueOf(paramsService.getParam("workingHoursSundayTo").getGetting()).intValue();
        } else {
            return Integer.valueOf(paramsService.getParam("workingHoursWeekDayTo").getGetting()).intValue();
        }
    }

    public LocalDateTime getNextShouldBeProcessedLocalDateTime(LocalDateTime lastShouldBeProcessedDate,
                                                               int endOfDayWorkingHour,
                                                               int minutesAppOrderShouldBeProcessed) {
        int newMinutes = lastShouldBeProcessedDate.getMinute() + minutesAppOrderShouldBeProcessed;
        if (newMinutes >= 60) {
            int newHour = lastShouldBeProcessedDate.getHour() + 1;
            if (newHour > endOfDayWorkingHour) {
                lastShouldBeProcessedDate.withDayOfYear(lastShouldBeProcessedDate.getDayOfYear() + 1);
                lastShouldBeProcessedDate.withSecond(0).withMinute(0)
                        .withHour(getStartOfWorkingDayHour(lastShouldBeProcessedDate.getDayOfWeek()));
            } else {
                lastShouldBeProcessedDate = lastShouldBeProcessedDate.withMinute(newMinutes - 60)
                        .withHour(newHour);
            }
        } else {
            lastShouldBeProcessedDate =
                    lastShouldBeProcessedDate.withMinute(newMinutes);
        }
        return lastShouldBeProcessedDate;
    }

    public AppOrder getLastAppOrder() {
        return appOrderRepository.findFirstByOrderByCreatedDateDesc();
    }

}
