package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OrderedShoe extends Audit {

    private int size;
    @OneToOne
    private Shoe shoe;
    private String comment;
    private boolean payed = false;
    private Boolean shouldNotBePayed = false;

    public OrderedShoe() {
    }

    public OrderedShoe(int size, Shoe shoe, Ordered ordered) {
        this.size = size;
        this.shoe = shoe;
    }

    public OrderedShoe(int size, Shoe shoe, String comment, Ordered ordered) {
        this.size = size;
        this.shoe = shoe;
        this.comment = comment;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public Boolean getShouldNotBePayed() {
        return shouldNotBePayed;
    }

    public void setShouldNotBePayed(Boolean shouldNotBePayed) {
        this.shouldNotBePayed = shouldNotBePayed;
    }
}
