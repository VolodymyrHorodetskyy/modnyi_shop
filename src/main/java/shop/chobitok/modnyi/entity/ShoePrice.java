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
    private LocalDateTime from;
    @Column
    private LocalDateTime to;
    @Column(nullable = false)
    private Double cost;
    @Column(nullable = false)
    private Double price;

    public ShoePrice() {
    }

    public ShoePrice(Shoe shoe, LocalDateTime from, Double cost, Double price) {
        this.shoe = shoe;
        this.from = from;
        this.cost = cost;
        this.price = price;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
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
