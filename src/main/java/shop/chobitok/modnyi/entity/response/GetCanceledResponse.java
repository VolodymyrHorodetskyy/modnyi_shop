package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.CanceledOrderReason;

import java.util.List;

public class GetCanceledResponse {

    private List<CanceledOrderReason> canceledOrderReasons;

    private Long total;


    public GetCanceledResponse(List<CanceledOrderReason> canceledOrderReasons, Long total) {
        this.canceledOrderReasons = canceledOrderReasons;
        this.total = total;
    }

    public List<CanceledOrderReason> getCanceledOrderReasons() {
        return canceledOrderReasons;
    }

    public void setCanceledOrderReasons(List<CanceledOrderReason> canceledOrderReasons) {
        this.canceledOrderReasons = canceledOrderReasons;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }


}
