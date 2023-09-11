package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class WholesaleOrder extends Audit {

    private String orderDescription;
    private Double cost;
    private Double shouldBePayed;
    private Double payed = 0d;
    private boolean completed;
    @ManyToOne
    private Company company;

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getShouldBePayed() {
        return shouldBePayed;
    }

    public void setShouldBePayed(Double shouldBePayed) {
        this.shouldBePayed = shouldBePayed;
    }

    public Double getPayed() {
        return payed;
    }

    public void setPayed(Double payed) {
        this.payed = payed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
