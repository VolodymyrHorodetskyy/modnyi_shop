package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class OurTTN extends Audit {

    @Column
    private String ttn;
    @Column
    private String senderPhone;
    @Column
    private String receiverPhone;
    @Column
    private LocalDateTime datePayedKeeping;
    @Column
    private Status status;
    @Column
    private boolean deleted = false;
    @Column
    private Long npAccountId;
    @Column
    private String cargoDescription;
    @Column
    private CancelReason cancelReason;
    @Column
    private String comment;
    private Double deliveryCost;


    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public LocalDateTime getDatePayedKeeping() {
        return datePayedKeeping;
    }

    public void setDatePayedKeeping(LocalDateTime datePayedKeeping) {
        this.datePayedKeeping = datePayedKeeping;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
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

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
}
