package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class AppOrderProcessing extends Audit {

    @OneToOne
    private AppOrder appOrder;

    @OneToOne
    private User user;

    @Column
    private AppOrderStatus oldStatus;

    @Column
    private AppOrderStatus newStatus;

    @Column
    private Boolean wasNew = false;
    private LocalDateTime remindOn;
    private boolean remindTomorrow;

    public AppOrderProcessing() {
    }

    public AppOrderProcessing(AppOrder appOrder, User user, AppOrderStatus oldStatus, AppOrderStatus newStatus, Boolean wasNew) {
        this.appOrder = appOrder;
        this.user = user;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.wasNew = wasNew;
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

    public Boolean getWasNew() {
        return wasNew;
    }

    public void setWasNew(Boolean wasNew) {
        this.wasNew = wasNew;
    }

    public AppOrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(AppOrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public AppOrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(AppOrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(LocalDateTime remindOn) {
        this.remindOn = remindOn;
    }

    public boolean isRemindTomorrow() {
        return remindTomorrow;
    }

    public void setRemindTomorrow(boolean remindTomorrow) {
        this.remindTomorrow = remindTomorrow;
    }
}
