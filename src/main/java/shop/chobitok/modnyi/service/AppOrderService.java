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
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppOrderService {

    private AppOrderRepository appOrderRepository;
    private OrderService orderService;
    private ClientRepository clientRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private DiscountService discountService;

    public AppOrderService(AppOrderRepository appOrderRepository, OrderService orderService, ClientRepository clientRepository, OrderRepository orderRepository, UserRepository userRepository, DiscountService discountService) {
        this.appOrderRepository = appOrderRepository;
        this.orderService = orderService;
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.discountService = discountService;
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

    public Map<AppOrderStatus, List<AppOrder>> getAll(Long id, String phoneAndName, String comment, String fromForNotReady, String fromForReady
            , String userId) {
        List<AppOrder> appOrdersNotReady = appOrderRepository.findAll(
                new AppOrderSpecification(id, phoneAndName, comment, DateHelper.formDate(fromForNotReady),
                        Arrays.asList(AppOrderStatus.Новий, AppOrderStatus.Не_Відповідає, AppOrderStatus.Чекаємо_оплату, AppOrderStatus.В_обробці), userId),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        List<AppOrder> appOrdersReady = appOrderRepository.findAll(
                new AppOrderSpecification(id, phoneAndName, comment, DateHelper.formDate(fromForReady),
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
        Map<AppOrderStatus, List<AppOrder>> appOrderMap = new LinkedHashMap<>();
        for (AppOrder appOrder : combinedAppOrders) {
            List<AppOrder> appOrders1 = appOrderMap.get(appOrder.getStatus());
            if (appOrders1 == null) {
                appOrders1 = new ArrayList<>();
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
        String message = null;
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
        changeStatus(appOrder, request.getStatus());
        String ttn = request.getTtn();
        if (!StringUtils.isEmpty(ttn)) {
            ttn = ttn.replaceAll("\\s+", "");
            appOrder.setTtn(ttn);
            message = orderService.importOrderFromTTNString(ttn, request.getUserId(), discountService.getById(request.getDiscountId()));
            String mail = appOrder.getMail();
            Ordered ordered = orderService.findByTTN(ttn);
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
        appOrder.setComment(request.getComment());
        return new ChangeAppOrderResponse(message, appOrderRepository.save(appOrder));
    }

    public AppOrder changeStatus(AppOrder appOrder, AppOrderStatus status) {
        if (appOrder.getStatus() != status) {
            appOrder.setPreviousStatus(appOrder.getStatus());
        }
        appOrder.setStatus(status);
        return appOrder;
    }
}
