package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class AppOrderNewProcessed extends Audit {

    @OneToOne
    private AppOrder appOrder;

    @OneToOne
    private User user;

    public AppOrderNewProcessed() {
    }

    public AppOrderNewProcessed(AppOrder appOrder, User user) {
        this.appOrder = appOrder;
        this.user = user;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
