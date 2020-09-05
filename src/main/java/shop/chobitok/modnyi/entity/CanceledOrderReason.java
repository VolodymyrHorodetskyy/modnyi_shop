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
    private String returnTtn;
    @Column
    private String newTtn;
    @Column
    private Status status;
    @Column(nullable = false)
    private boolean manual = false;

    public CanceledOrderReason() {
    }

    public CanceledOrderReason(Ordered ordered, CancelReason reason, String comment, String newTtn) {
        this.ordered = ordered;
        this.reason = reason;
        this.comment = comment;
        this.newTtn = newTtn;
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

    public String getReturnTtn() {
        return returnTtn;
    }

    public void setReturnTtn(String returnTtn) {
        this.returnTtn = returnTtn;
    }

    public String getNewTtn() {
        return newTtn;
    }

    public void setNewTtn(String newTtn) {
        this.newTtn = newTtn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
