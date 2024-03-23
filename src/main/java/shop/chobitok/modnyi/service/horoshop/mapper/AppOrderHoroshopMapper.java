package shop.chobitok.modnyi.service.horoshop.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.service.AppOrderService;
import shop.chobitok.modnyi.service.horoshop.response.GetOrdersResponse;
import shop.chobitok.modnyi.service.horoshop.response.Order;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppOrderHoroshopMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final AppOrderService appOrderService;

    public AppOrderHoroshopMapper(AppOrderService appOrderService) {
        this.appOrderService = appOrderService;
    }

    public List<AppOrder> convertToAppOrderFilteringExistingAppOrders(GetOrdersResponse getOrdersResponse) {

        if (getOrdersResponse == null || getOrdersResponse.getResponse() == null || getOrdersResponse.getResponse().getOrders() == null) {
            return null;
        }
        return getOrdersResponse.getResponse().getOrders().stream()
                .filter(order -> appOrderService.getByHoroshopOrderId(order.orderId) == null)
                .map(o -> convertToAppOrder(o, getOrdersResponse.getSite()))
                .collect(Collectors.toUnmodifiableList());
    }

    private AppOrder convertToAppOrder(Order order, String site) {
        if (order == null) {
            return null;
        }
        AppOrder appOrder = new AppOrder();
        appOrder.setStatus(AppOrderStatus.Новий);
        appOrder.setHoroshopOrderId(order.orderId);
        appOrder.setName(order.deliveryName);
        appOrder.setMail(order.deliveryEmail);
        appOrder.setPhone(order.deliveryPhone);
        appOrder.setCityForFb(order.deliveryCityStable);
        appOrder.setDelivery(order.deliveryAddress);
        appOrder.setSite(site);
        try {
            appOrder.setHoroshopProductsJson(objectMapper.writeValueAsString(order.products));
            appOrder.setHoroshopDeliveryDataJson(objectMapper.writeValueAsString(order.deliveryData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return appOrder;
    }
}
