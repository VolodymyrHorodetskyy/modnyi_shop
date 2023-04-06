package shop.chobitok.modnyi.service.horoshop.response;

import java.util.List;

public class OrdersResponse {

    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
