package shop.chobitok.modnyi.entity.response;

public class AmountsInfoResponse {

    private Integer newAppOrders;
    private Integer canceledWithoutReason;

    public AmountsInfoResponse(Integer newAppOrders, Integer canceledWithoutReason) {
        this.newAppOrders = newAppOrders;
        this.canceledWithoutReason = canceledWithoutReason;
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
}
