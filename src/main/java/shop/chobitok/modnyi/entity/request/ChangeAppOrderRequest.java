package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.AppOrderStatus;

public class ChangeAppOrderRequest {

    private Long id;
    private String comment;
    private AppOrderStatus status;
    private String ttn;
    private Long userId;
    private Long discountId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppOrderStatus getStatus() {
        return status;
    }

    public void setStatus(AppOrderStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }
}
