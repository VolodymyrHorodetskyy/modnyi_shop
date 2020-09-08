package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.CancelReason;

public class CancelOrderRequest {

    private CancelReason reason;

    private String comment;

    private String newTTN;

    private String returnTTN;

    public String getNewTTN() {
        return newTTN;
    }

    public void setNewTTN(String newTTN) {
        this.newTTN = newTTN;
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

    public String getReturnTTN() {
        return returnTTN;
    }

    public void setReturnTTN(String returnTTN) {
        this.returnTTN = returnTTN;
    }
}
