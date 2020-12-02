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
}
