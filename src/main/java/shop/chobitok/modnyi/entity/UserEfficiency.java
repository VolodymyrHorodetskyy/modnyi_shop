package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserEfficiency extends Audit {

    @OneToOne
    private User user;

    private int minutes;

    private int processingEfficiency;

    @OneToOne
    private AppOrder appOrder;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getProcessingEfficiency() {
        return processingEfficiency;
    }

    public void setProcessingEfficiency(int processingEfficiency) {
        this.processingEfficiency = processingEfficiency;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }
}
