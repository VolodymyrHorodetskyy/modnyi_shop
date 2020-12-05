package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class PayedOrdered extends Audit {

    private String ttn;
    private Double sum;
    private boolean counted = false;

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public boolean isCounted() {
        return counted;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }
}
