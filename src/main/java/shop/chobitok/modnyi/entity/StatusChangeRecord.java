package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class StatusChangeRecord extends Audit {

    @ManyToOne
    private Ordered ordered;
    private Status previousStatus;
    private Status newStatus;

    public StatusChangeRecord() {
    }

    public StatusChangeRecord(Ordered ordered, Status previousStatus, Status newStatus) {
        this.ordered = ordered;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }
}
