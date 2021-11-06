package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AppOrderToPixel extends Audit {

    @OneToOne
    private AppOrder appOrder;
    private int trying = 0;
    private boolean sent;

    public AppOrderToPixel() {
    }

    public AppOrderToPixel(AppOrder appOrder) {
        this.appOrder = appOrder;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }

    public int getTrying() {
        return trying;
    }

    public void setTrying(int trying) {
        this.trying = trying;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
