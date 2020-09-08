package shop.chobitok.modnyi.entity.request;


public class CancelOrderWithOrderRequest extends CancelOrderRequest {

    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
