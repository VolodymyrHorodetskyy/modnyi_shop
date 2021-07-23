package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserEfficiency extends Audit {

    @OneToOne
    private User user;

    @OneToOne
    private AppOrder appOrder;

    private int minutes;

    private double processingEfficiency;

    public UserEfficiency() {
    }

    public UserEfficiency(User user, int minutes, double processingEfficiency, AppOrder appOrder) {
        this.user = user;
        this.minutes = minutes;
        this.processingEfficiency = processingEfficiency;
        this.appOrder = appOrder;
    }

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

    public double getProcessingEfficiency() {
        return processingEfficiency;
    }

    public void setProcessingEfficiency(double processingEfficiency) {
        this.processingEfficiency = processingEfficiency;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }
}
