package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.CancelReason;

public class CancelOrderRequest {

    private Long orderId;

    private CancelReason reason;

    private String comment;

    private String newTTN;

    public String getNewTTN() {
        return newTTN;
    }

    public void setNewTTN(String newTTN) {
        this.newTTN = newTTN;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public CancelReason getReason() {
        return reason;
    }

    public void setReason(CancelReason reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
