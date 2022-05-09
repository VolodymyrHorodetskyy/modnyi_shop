package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class PayedOrdered extends Audit {

    @OneToOne
    private Ordered ordered;
    @OneToOne
    private OrderedShoe orderedShoe;
    private Double sum;
    private boolean counted = false;

    public OrderedShoe getOrderedShoe() {
        return orderedShoe;
    }

    public void setOrderedShoe(OrderedShoe orderedShoe) {
        this.orderedShoe = orderedShoe;
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

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }
}
