package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.CancelReason;

public class AddOurTtnRequest {

    private Long npAccountId;
    private String ttn;
    private String cargoDescription;
    private CancelReason cancelReason;
    private String comment;

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public CancelReason getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(CancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
    }
}
