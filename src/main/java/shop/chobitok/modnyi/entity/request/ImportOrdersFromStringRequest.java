package shop.chobitok.modnyi.entity.request;

public class ImportOrdersFromStringRequest {

    private String ttns;
    private Long discountId;
    private Long userId;

    public String getTtns() {
        return ttns;
    }

    public void setTtns(String ttns) {
        this.ttns = ttns;
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
