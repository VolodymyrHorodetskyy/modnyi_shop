package shop.chobitok.modnyi.entity.request;

public class ImportOrdersFromStringRequest {

    private String ttns;
    private Long discountId;
    private Long userId;
    private Long npAccountId;

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

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }
}
