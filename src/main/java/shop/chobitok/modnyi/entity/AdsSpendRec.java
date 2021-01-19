package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class AdsSpendRec extends Audit {

    private LocalDate start;
    private LocalDate end;
    private Double spendSum;

    public AdsSpendRec() {
    }

    public AdsSpendRec(LocalDate start, LocalDate end, Double spendSum) {
        this.start = start;
        this.end = end;
        this.spendSum = spendSum;
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

    public Double getSpendSum() {
        return spendSum;
    }

    public void setSpendSum(Double spendSum) {
        this.spendSum = spendSum;
    }
}
