package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class DayCosts extends Audit{

    private LocalDate spendDate;
    private Double spendSum;
    @OneToOne
    private Variants spendType;
    private String comment;
    @ManyToOne
    private Costs costs;

    public DayCosts() {
    }

    public DayCosts(LocalDate spendDate, Double spendSum, Variants spendType, String comment, Costs costs) {
        this.spendDate = spendDate;
        this.spendSum = spendSum;
        this.spendType = spendType;
        this.comment = comment;
        this.costs = costs;
    }

    public LocalDate getSpendDate() {
        return spendDate;
    }

    public void setSpendDate(LocalDate spendDate) {
        this.spendDate = spendDate;
    }

    public Double getSpendSum() {
        return spendSum;
    }

    public void setSpendSum(Double spendSum) {
        this.spendSum = spendSum;
    }

    public Variants getSpendType() {
        return spendType;
    }

    public void setSpendType(Variants spendType) {
        this.spendType = spendType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Costs getCosts() {
        return costs;
    }

    public void setCosts(Costs costs) {
        this.costs = costs;
    }
}
