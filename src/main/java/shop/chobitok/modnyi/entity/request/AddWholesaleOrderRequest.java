package shop.chobitok.modnyi.entity.request;

public class AddWholesaleOrderRequest {

    private String orderDescription;
    private Double cost;
    private Double shouldBePayed;
    private Double payed = 0d;
    private boolean completed;
    private Long companyId;

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getShouldBePayed() {
        return shouldBePayed;
    }

    public void setShouldBePayed(Double shouldBePayed) {
        this.shouldBePayed = shouldBePayed;
    }

    public Double getPayed() {
        return payed;
    }

    public void setPayed(Double payed) {
        this.payed = payed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
