package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Entity
public class DaySpendRec extends Audit {

    private LocalDate spendDate;
    private Double spendSum;
    @OneToMany
    private List<SpendRec> spendRecords;

    public DaySpendRec() {
    }

    public DaySpendRec(LocalDate spendDate, Double spendSum) {
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

    public List<SpendRec> getSpendRecords() {
        return spendRecords;
    }

    public void setSpendRecords(List<SpendRec> spendRecords) {
        this.spendRecords = spendRecords;
    }
}
