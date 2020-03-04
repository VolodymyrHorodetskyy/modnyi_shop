package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.Status;

public class UpdateOrderRequest {

    private String name;
    private String lastName;
    private String middleName;
    private Status status;
    private String address;
    private String phone;
    private Integer size;
    private Long shoe;
    private String notes;
    private Double price;
    private Double prepayment;
    private boolean full_payment;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(Double prepayment) {
        this.prepayment = prepayment;
    }

    public boolean isFull_payment() {
        return full_payment;
    }

    public void setFull_payment(boolean full_payment) {
        this.full_payment = full_payment;
    }
}
