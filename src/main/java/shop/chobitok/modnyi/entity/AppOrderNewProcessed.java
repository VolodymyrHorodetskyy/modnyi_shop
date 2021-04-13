package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AppOrderNewProcessed extends Audit {

    @OneToOne
    private AppOrder appOrder;

    public AppOrderNewProcessed() {
    }

    public AppOrderNewProcessed(AppOrder appOrder) {
        this.appOrder = appOrder;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }
}
