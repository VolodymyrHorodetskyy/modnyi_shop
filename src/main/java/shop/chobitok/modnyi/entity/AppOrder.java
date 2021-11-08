package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppOrder extends Audit {

    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String validatedPhones;
    @Column
    private String mail;
    @Column
    private boolean dontCall = false;
    @ElementCollection
    private List<String> products;
    @Column
    private Double amount;
    @Column
    private String info;
    @Column(nullable = false)
    private AppOrderStatus status;
    @Column
    private AppOrderStatus previousStatus;
    @Column
    private String comment;
    @Column
    private String ttn;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
    @Column
    private LocalDateTime dateAppOrderShouldBeProcessed;
    @Column
    private AppOrderCancellationReason cancellationReason;
    @Column
    private LocalDateTime remindOn;
    @Column
    private String delivery;
    private String domain;
    @OneToOne
    private Pixel pixel;
    private String fbp;
    private String fbc;
    private String clientUserAgent;
    private String eventSourceUrl;

    private String cityForFb;
    private String firstNameForFb;
    private String lastNameForFb;

    public AppOrder() {
    }

    public AppOrder(String info) {
        this.info = info;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isDontCall() {
        return dontCall;
    }

    public void setDontCall(boolean dontCall) {
        this.dontCall = dontCall;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public AppOrderStatus getStatus() {
        return status;
    }

    public void setStatus(AppOrderStatus status) {
        this.status = status;
    }

    public AppOrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(AppOrderStatus previousStatus) {
        this.previousStatus = previousStatus;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateAppOrderShouldBeProcessed() {
        return dateAppOrderShouldBeProcessed;
    }

    public void setDateAppOrderShouldBeProcessed(LocalDateTime dateAppOrderShouldBeProcessed) {
        this.dateAppOrderShouldBeProcessed = dateAppOrderShouldBeProcessed;
    }

    public AppOrderCancellationReason getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(AppOrderCancellationReason cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(LocalDateTime remindOn) {
        this.remindOn = remindOn;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Pixel getPixel() {
        return pixel;
    }

    public void setPixel(Pixel pixel) {
        this.pixel = pixel;
    }

    public String getFbp() {
        return fbp;
    }

    public void setFbp(String fbp) {
        this.fbp = fbp;
    }

    public String getFbc() {
        return fbc;
    }

    public void setFbc(String fbc) {
        this.fbc = fbc;
    }

    public String getValidatedPhones() {
        return validatedPhones;
    }

    public void setValidatedPhones(String validatedPhones) {
        this.validatedPhones = validatedPhones;
    }

    public String getClientUserAgent() {
        return clientUserAgent;
    }

    public void setClientUserAgent(String clientUserAgent) {
        this.clientUserAgent = clientUserAgent;
    }

    public String getEventSourceUrl() {
        return eventSourceUrl;
    }

    public void setEventSourceUrl(String eventSourceUrl) {
        this.eventSourceUrl = eventSourceUrl;
    }

    public String getCityForFb() {
        return cityForFb;
    }

    public void setCityForFb(String cityForFb) {
        this.cityForFb = cityForFb;
    }

    public String getFirstNameForFb() {
        return firstNameForFb;
    }

    public void setFirstNameForFb(String firstNameForFb) {
        this.firstNameForFb = firstNameForFb;
    }

    public String getLastNameForFb() {
        return lastNameForFb;
    }

    public void setLastNameForFb(String lastNameForFb) {
        this.lastNameForFb = lastNameForFb;
    }
}
