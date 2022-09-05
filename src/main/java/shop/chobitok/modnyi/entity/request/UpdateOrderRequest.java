package shop.chobitok.modnyi.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import shop.chobitok.modnyi.entity.Status;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateOrderRequest {

    private String name;
    private String lastName;
    private String middleName;
    private String mail;
    private Status status;
    private String address;
    private String phone;
    private Integer size;
    private List<Long> shoes;
    private String notes;
    private Double price;
    private Double prepayment;
    private boolean full_payment;
    private Long userId;
    private String postComment;
    private Boolean urgent;
    private Long discountId;
    private boolean allCorrect;
    private Long sourceOfOrderId;
    private Boolean shouldNotBePayed;

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

    public List<Long> getShoes() {
        return shoes;
    }

    public void setShoes(List<Long> shoes) {
        this.shoes = shoes;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPostComment() {
        return postComment;
    }

    public void setPostComment(String postComment) {
        this.postComment = postComment;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public boolean isAllCorrect() {
        return allCorrect;
    }

    public void setAllCorrect(boolean allCorrect) {
        this.allCorrect = allCorrect;
    }

    public Long getSourceOfOrderId() {
        return sourceOfOrderId;
    }

    public void setSourceOfOrderId(Long sourceOfOrderId) {
        this.sourceOfOrderId = sourceOfOrderId;
    }

    public Boolean getShouldNotBePayed() {
        return shouldNotBePayed;
    }

    public void setShouldNotBePayed(Boolean shouldNotBePayed) {
        this.shouldNotBePayed = shouldNotBePayed;
    }
}
