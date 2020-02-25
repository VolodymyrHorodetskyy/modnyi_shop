package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.Status;

import javax.validation.constraints.NotEmpty;

public class CreateOrderRequest {

    @NotEmpty
    private String ttn;
    @NotEmpty
    private String phone;

    private Status status;

    private Double price;
    @NotEmpty
    private String address;

    private Integer size;

    private Long shoe;

    private String comment;

    private Double prepayment;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;

    private String middleName;

    private String notes;

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

    public Long getShoe() {
        return shoe;
    }

    public void setShoe(Long shoe) {
        this.shoe = shoe;
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
}
