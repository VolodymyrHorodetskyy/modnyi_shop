package shop.chobitok.modnyi.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppOrderService {

    private AppOrderRepository appOrderRepository;
    private OrderService orderService;

    public AppOrderService(AppOrderRepository appOrderRepository, OrderService orderService) {
        this.appOrderRepository = appOrderRepository;
        this.orderService = orderService;
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
                    appOrder.setPhone(s1.substring(s1.indexOf("=") + 1));
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

    public Map<AppOrderStatus, List<AppOrder>> getAll(Long id, String phoneAndName, String from) {
        List<AppOrder> appOrders = appOrderRepository.findAll(new AppOrderSpecification(id, phoneAndName, DateHelper.formDate(from)), Sort.by("createdDate"));
        Map<AppOrderStatus, List<AppOrder>> appOrderMap = new HashMap<>();
        for (AppOrder appOrder : appOrders) {
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
        AppOrder appOrder = appOrderRepository.getOne(request.getId());
        String message = null;
        if (appOrder == null) {
            throw new ConflictException("AppOrder not found");
        }
        changeStatus(appOrder, request.getStatus());
        String ttn = request.getTtn();
        appOrder.setTtn(ttn);
        if (!StringUtils.isEmpty(ttn)) {
            message = orderService.importOrderFromTTNString(ttn);
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
