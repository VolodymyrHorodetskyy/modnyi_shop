package shop.chobitok.modnyi.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.request.AddCommentToAppOrderRequest;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderStatusAndCommentRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.AppOrderRepository;
import shop.chobitok.modnyi.specification.AppOrderSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppOrderService {

    private AppOrderRepository appOrderRepository;

    public AppOrderService(AppOrderRepository appOrderRepository) {
        this.appOrderRepository = appOrderRepository;
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

    public AppOrder changeAppOrderStatus(ChangeAppOrderStatusAndCommentRequest request) {
        AppOrder appOrder = appOrderRepository.getOne(request.getId());
        if (appOrder == null) {
            throw new ConflictException("AppOrder not found");
        }
        if (appOrder.getStatus() != request.getStatus()) {
            appOrder.setPreviousStatus(appOrder.getStatus());
        }
        appOrder.setStatus(request.getStatus());
        appOrder.setComment(request.getComment());
        return appOrderRepository.save(appOrder);
    }

    public AppOrder addComment(AddCommentToAppOrderRequest request) {
        AppOrder appOrder = appOrderRepository.getOne(request.getId());
        if (appOrder == null) {
            throw new ConflictException("AppOrder not found");
        }
        appOrder.setComment(request.getComment());
        return appOrderRepository.save(appOrder);
    }

}
