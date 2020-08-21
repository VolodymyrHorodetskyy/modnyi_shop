package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.Status;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {

    private String ttn;
    @NotEmpty
    private String phone;
    private String mail;
    private Status status;
    private Double price;
    private String address;
    private Integer size;
    private List<Long> shoes;
    private String comment;
    private Double prepayment;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;
    private String middleName;
    private String notes;
    private String orderBy;
    private boolean fullpayment;


    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Long> getShoes() {
        return shoes;
    }

    public void setShoes(List<Long> shoes) {
        this.shoes = shoes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(Double prepayment) {
        this.prepayment = prepayment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isFullpayment() {
        return fullpayment;
    }

    public void setFullpayment(boolean fullpayment) {
        this.fullpayment = fullpayment;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
