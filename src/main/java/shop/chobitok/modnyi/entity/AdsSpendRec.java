package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class AdsSpendRec extends Audit {

    private LocalDate spendDate;
    private Double spendSum;

    public AdsSpendRec() {
    }

    public AdsSpendRec(LocalDate spendDate, Double spendSum) {
        this.spendDate = spendDate;
        this.spendSum = spendSum;
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
}
