package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ShoePrice extends Audit {

    @ManyToOne
    private Shoe shoe;
    @Column(nullable = false)
    private LocalDateTime fromDate;
    @Column
    private LocalDateTime toDate;
    @Column(nullable = false)
    private Double cost;
    @Column(nullable = false)
    private Double price;

    public ShoePrice() {
    }

    public ShoePrice(Shoe shoe, LocalDateTime fromDate, Double cost, Double price) {
        this.shoe = shoe;
        this.fromDate = fromDate;
        this.cost = cost;
        this.price = price;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
