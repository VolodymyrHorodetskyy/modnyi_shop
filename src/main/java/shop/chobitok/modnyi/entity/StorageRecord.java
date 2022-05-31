package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class StorageRecord extends Audit {

    @ManyToOne
    private Shoe shoe;
    private Integer size;
    @OneToOne
    private CanceledOrderReason canceledOrderReason;
    private String comment;
    private boolean available = true;

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public CanceledOrderReason getCanceledOrderReason() {
        return canceledOrderReason;
    }

    public void setCanceledOrderReason(CanceledOrderReason canceledOrderReason) {
        this.canceledOrderReason = canceledOrderReason;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
