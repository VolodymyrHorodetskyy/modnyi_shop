package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
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
}
