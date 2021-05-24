package shop.chobitok.modnyi.entity.request;

public class RemoveShoeFromOrderRequest {

    private Long orderId;
    private Long shoeId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }
}
