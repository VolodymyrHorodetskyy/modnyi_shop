package shop.chobitok.modnyi.entity;

import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Costs extends Audit {

    private LocalDate start;
    private LocalDate end;
    private Double spend;
    @OneToOne
    private Variants spendType;
    private String comment;

    public Costs() {
    }

    public Costs(LocalDate start, LocalDate end, Variants spendType, SaveAdsSpendsRequest spendsRequest) {
        this.start = start;
        this.end = end;
        this.spend = spendsRequest.getSpends();
        this.spendType = spendType;
        this.comment = spendsRequest.getDescription();
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Double getSpend() {
        return spend;
    }

    public void setSpend(Double spend) {
        this.spend = spend;
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
}
