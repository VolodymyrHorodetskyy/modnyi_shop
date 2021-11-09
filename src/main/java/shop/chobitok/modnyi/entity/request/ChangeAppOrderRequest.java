package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.AppOrderCancellationReason;
import shop.chobitok.modnyi.entity.AppOrderStatus;

public class ChangeAppOrderRequest {

    private Long id;
    private String comment;
    private AppOrderStatus status;
    private String ttn;
    private Long userId;
    private Long discountId;
    private AppOrderCancellationReason cancellationReason;
    private int remindAt;
    private boolean remindTomorrow;

    private String name;
    private String lastName;
    private String phones;
    private String city;

    private boolean dataValid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppOrderStatus getStatus() {
        return status;
    }

    public void setStatus(AppOrderStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public AppOrderCancellationReason getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(AppOrderCancellationReason cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public int getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(int remindAt) {
        this.remindAt = remindAt;
    }

    public boolean isRemindTomorrow() {
        return remindTomorrow;
    }

    public void setRemindTomorrow(boolean remindTomorrow) {
        this.remindTomorrow = remindTomorrow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isDataValid() {
        return dataValid;
    }

    public void setDataValid(boolean dataValid) {
        this.dataValid = dataValid;
    }
}
