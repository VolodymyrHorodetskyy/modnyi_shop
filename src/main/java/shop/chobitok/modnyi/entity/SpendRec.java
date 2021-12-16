package shop.chobitok.modnyi.entity;

import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class SpendRec extends Audit {

    private LocalDate start;
    private LocalDate finish;
    private Double spends;
    private SpendType spendType;
    private String comment;

    public SpendRec(LocalDate start, LocalDate finish, SaveAdsSpendsRequest saveAdsSpendsRequest) {
        this.start = start;
        this.finish = finish;
        this.spends = saveAdsSpendsRequest.getSpends();
  //      this.spendType = saveAdsSpends.getSpendType();
        this.comment = saveAdsSpendsRequest.getDescription();
    }

    public SpendRec() {
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }

    public Double getSpends() {
        return spends;
    }

    public void setSpends(Double spends) {
        this.spends = spends;
    }

    public SpendType getSpendType() {
        return spendType;
    }

    public void setSpendType(SpendType spendType) {
        this.spendType = spendType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
