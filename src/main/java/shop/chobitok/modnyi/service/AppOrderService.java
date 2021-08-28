package shop.chobitok.modnyi.service;

import com.google.api.client.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.*;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
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
    private UserEfficiencyService userEfficiencyService;
    private UserLoggedInRepository userLoggedInRepository;

    public AppOrderService(AppOrderRepository appOrderRepository, OrderService orderService, ClientRepository clientRepository, OrderRepository orderRepository, UserRepository userRepository, DiscountService discountService, AppOrderProcessingRepository appOrderProcessingRepository, ParamsService paramsService, UserEfficiencyService userEfficiencyService, UserLoggedInRepository userLoggedInRepository) {
        this.appOrderRepository = appOrderRepository;
        this.orderService = orderService;
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.discountService = discountService;
        this.appOrderProcessingRepository = appOrderProcessingRepository;
        this.paramsService = paramsService;
        this.userEfficiencyService = userEfficiencyService;
        this.userLoggedInRepository = userLoggedInRepository;
    }

    public AppOrder catchOrder(String s) throws UnsupportedEncodingException {
        AppOrder appOrder = new AppOrder();
        String decoded = URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        appOrder.setInfo(decoded);
        Map<String, List<String>> splittedUrl = splitQuery(decoded);
        appOrder.setName(getValue(splittedUrl.get("name")));
        appOrder.setPhone(getValue(splittedUrl.get("phone")));
        appOrder.setMail(getValue(splittedUrl.get("Email")));
        appOrder.setDontCall(getValue(splittedUrl.get("dont_call")) != null
                && !getValue(splittedUrl.get("dont_call")).isEmpty());
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

    private Map<String, List<String>> splitQuery(String params) {
        if (Strings.isNullOrEmpty(params)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(params.split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        SimpleImmutableEntry simpleImmutableEntry = null;
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        try {
            simpleImmutableEntry = new SimpleImmutableEntry<>(
                    URLDecoder.decode(key, "UTF-8"),
                    URLDecoder.decode(value, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return simpleImmutableEntry;
    }

    public AppOrder findFirstShouldBeProcessedAppOrderByUserId(Long userId) {
        AppOrder appOrder = appOrderRepository.findFirstByStatusInAndPreviousStatusIsNullAndUserIdOrderByDateAppOrderShouldBeProcessedDesc(
                Arrays.asList(AppOrderStatus.Новий), userId);
        if (appOrder == null) {
            appOrder = appOrderRepository.findFirstByStatusInAndUserIdOrderByCreatedDateDesc(
                    Arrays.asList(AppOrderStatus.Новий), userId);
        }
        return appOrder;
    }

    public void assignAppOrderToUserAndSetShouldBeProcessedTime(AppOrder appOrder) {
        AppOrder lastAppOrder = getLastAppOrder();
        List<UserLoggedIn> usersLoggedIn = userLoggedInRepository.findAllByActiveTrueAndCreatedDateGreaterThanEqual(
                DateHelper.formLocalDateTimeStartOfTheDay(now()));
        User userToAssign = usersLoggedIn.stream().filter(userLoggedIn -> userLoggedIn.isActive() &&
                !userLoggedIn.getUser().getId().equals(lastAppOrder.getUser())).map(userLoggedIn -> userLoggedIn.getUser())
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
                Arrays.asList(AppOrderStatus.Новий));
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
        LocalDateTime availableShouldBeProcessedDateTime = null;
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
        if (!StringUtils.isEmpty(userId)) {
            List<AppOrder> newAppOrders = appOrderRepository.findAll(new AppOrderSpecification(phoneAndName, comment, Arrays.asList(AppOrderStatus.Новий), true));
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

        if (appOrder == null) {
            throw new ConflictException("AppOrder not found");
        }
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        } else {
            throw new ConflictException("User not found");
        }
        appOrder.setUser(user);
        String ttn = request.getTtn();
        String message = processAppOrderTtn(ttn, appOrder, request, user);
        changeStatus(appOrder, user, request.getStatus());
        appOrder.setTtn(ttn);
        appOrder.setComment(request.getComment());
        return new ChangeAppOrderResponse(message, appOrderRepository.save(appOrder));
    }


    public String processAppOrderTtn(String ttn, AppOrder appOrder, ChangeAppOrderRequest request,
                                     User user) {
        String message = null;
        if (!StringUtils.isEmpty(ttn)) {
            ttn = ttn.replaceAll("\\s+", "");
            message = orderService.importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId()));
            appOrder.setTtn(ttn);
            String mail = appOrder.getMail();
            Ordered ordered = orderService.findByTTN(ttn);
            if (ordered == null) {
                return null;
            }
            if (!StringUtils.isEmpty(mail)) {
                Client client = ordered.getClient();
                client.setMail(mail);
                clientRepository.save(client);
            }
            if (!StringUtils.isEmpty(request.getComment())) {
                if (StringUtils.isEmpty(ordered.getNotes())) {
                    ordered.setNotes(request.getComment());
                    orderRepository.save(ordered);
                }
            }
            ordered.setUser(user);
            orderRepository.save(ordered);
        }
        return message;
    }

    public AppOrder changeStatus(AppOrder appOrder, User user, AppOrderStatus status) {
        // userEfficiencyService.determineEfficiency(appOrder, user);
        if (appOrder.getStatus() != status) {
            appOrder.setPreviousStatus(appOrder.getStatus());
            appOrderProcessingRepository.save(new AppOrderProcessing(
                    appOrder, user, appOrder.getStatus(), status, appOrder.getPreviousStatus() == null
            ));
        }
        appOrder.setStatus(status);
        return appOrder;
    }

    public String importNotImported() {
        StringBuilder result = new StringBuilder();
        List<AppOrder> appOrders = appOrderRepository.findByTtnIsNotNullAndLastModifiedDateIsGreaterThan(now().minusDays(3));
        for (AppOrder appOrder : appOrders) {
            if (!StringUtils.isEmpty(appOrder.getTtn()) && orderRepository.findOneByAvailableTrueAndTtn(appOrder.getTtn()) == null) {
                result.append(orderService.importOrderFromTTNString(appOrder.getTtn(), appOrder.getUser().getId(), null));
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
