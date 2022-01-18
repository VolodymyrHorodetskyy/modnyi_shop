package shop.chobitok.modnyi.entity.response;

public class AmountsInfoResponse {

    private Integer newAppOrders;
    private Integer canceledWithoutReason;
    private Integer ourTtnsAmount;
    private Integer orderMistakes;

    public AmountsInfoResponse(Integer newAppOrders, Integer canceledWithoutReason, Integer ourTtnsAmount, Integer orderMistakes) {
        this.newAppOrders = newAppOrders;
        this.canceledWithoutReason = canceledWithoutReason;
        this.ourTtnsAmount = ourTtnsAmount;
        this.orderMistakes = orderMistakes;
    }

    public AmountsInfoResponse() {
    }

    public Integer getNewAppOrders() {
        return newAppOrders;
    }

    public void setNewAppOrders(Integer newAppOrders) {
        this.newAppOrders = newAppOrders;
    }

    public Integer getCanceledWithoutReason() {
        return canceledWithoutReason;
    }

    public void setCanceledWithoutReason(Integer canceledWithoutReason) {
        this.canceledWithoutReason = canceledWithoutReason;
    }

    public Integer getOurTtnsAmount() {
        return ourTtnsAmount;
    }

    public void setOurTtnsAmount(Integer ourTtnsAmount) {
        this.ourTtnsAmount = ourTtnsAmount;
    }

    public Integer getOrderMistakes() {
        return orderMistakes;
    }

    public void setOrderMistakes(Integer orderMistakes) {
        this.orderMistakes = orderMistakes;
    }
}
