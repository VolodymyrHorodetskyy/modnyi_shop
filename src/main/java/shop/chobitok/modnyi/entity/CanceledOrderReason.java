package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CanceledOrderReason extends Audit {

    @ManyToOne
    private Ordered ordered;
    @Column
    private CancelReason reason;
    @Column
    private String comment;
    @Column
    private String ttn;

    public CanceledOrderReason() {
    }

    public CanceledOrderReason(Ordered ordered, CancelReason reason, String comment, String ttn) {
        this.ordered = ordered;
        this.reason = reason;
        this.comment = comment;
        this.ttn = ttn;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
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

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }
}
