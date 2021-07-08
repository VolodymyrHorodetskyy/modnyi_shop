package shop.chobitok.modnyi.service;

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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static shop.chobitok.modnyi.util.DateHelper.formDateFromOrGetDefault;

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

    public AppOrderService(AppOrderRepository appOrderRepository, OrderService orderService, ClientRepository clientRepository, OrderRepository orderRepository, UserRepository userRepository, DiscountService discountService, AppOrderProcessingRepository appOrderProcessingRepository, ParamsService paramsService) {
        this.appOrderRepository = appOrderRepository;
        this.orderService = orderService;
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.discountService = discountService;
        this.appOrderProcessingRepository = appOrderProcessingRepository;
        this.paramsService = paramsService;
    }

    public AppOrder catchOrder(String s) {
        AppOrder appOrder = new AppOrder();
        try {
            String decoded = URLDecoder.decode(s, StandardCharsets.UTF_8.name());
            appOrder.setInfo(decoded);
            String[] splitted = decoded.split("&");
            appOrder.setName(splitted[0].substring(splitted[0].indexOf("=") + 1));
            for (String s1 : splitted) {
                if (s1.contains("phone")) {
                    appOrder.setPhone(s1.substring(s1.indexOf("=") + 1).replaceAll("[^0-9]", ""));
                } else if (s1.contains("Email")) {
                    appOrder.setMail(s1.substring(s1.indexOf("=") + 1));
                } else if (s1.contains("dont_call")) {
                    appOrder.setDontCall(true);
                } else if (s1.contains("payment")) {
                    String json = s1.substring(s1.indexOf("=") + 1);
                    JSONObject jsonObject = new JSONObject(json);
                    appOrder.setAmount(jsonObject.getDouble("amount"));
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    List<String> orders = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        orders.add(jsonArray.get(i).toString());
                    }
                    appOrder.setProducts(orders);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        appOrder.setStatus(AppOrderStatus.Новий);
        return appOrderRepository.save(appOrder);
    }

    public void setShouldBeProcessedAppOrderDateAndAssignToUser(List<User> users) {
        if (users.size() > 0) {
            int getUserIndex = 0;
            List<AppOrder> appOrders = appOrderRepository.findByStatusInOrderByCreatedDateDesc(
                    Arrays.asList(AppOrderStatus.Новий));
            int startOfDayWorkingHour = getStartOfWorkingDayHour(LocalDateTime.now().getDayOfWeek());
            LocalDateTime shouldBeProcessedDate = LocalDateTime.now().withHour(startOfDayWorkingHour).withMinute(0).withSecond(0);
            int endOfDayWorkingHour = getEndOfWorkingDayHour(LocalDateTime.now().getDayOfWeek());
            int minutesAppOrderShouldBeProcessed =
                    Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
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
        }
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
            combinedAppOrders = Stream.concat(appOrdersNotReady.stream(), appOrdersReady.stream()).collect(Collectors.toList());
            combinedAppOrders = Stream.concat(newAppOrders.stream(), combinedAppOrders.stream()).collect(Collectors.toList());
        } else {
            combinedAppOrders = Stream.concat(appOrdersNotReady.stream(), appOrdersReady.stream()).collect(Collectors.toList());
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
        List<AppOrder> appOrders = appOrderRepository.findByTtnIsNotNullAndLastModifiedDateIsGreaterThan(LocalDateTime.now().minusDays(3));
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

}
