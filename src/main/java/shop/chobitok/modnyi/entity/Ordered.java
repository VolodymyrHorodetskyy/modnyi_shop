package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Ordered extends Audit {

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Shoe> orderedShoes;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @Column
    private Integer size;

    @Column
    private String ttn;

    @Column
    private String notes;

    @Column
    private Status status;

    @Column
    private Integer statusNP;

    @Column
    private Double prePayment;

    @Column
    private Double price = 0d;

    @Column
    private String address;

    @Column
    private String postComment;

    @Column
    private LocalDateTime lastTransactionDateTime;

    @Column
    private Double returnSumNP;

    @Column
    private String nameAndSurnameNP;

    @Column
    private String lastCreatedOnTheBasisDocumentTypeNP;

    @Column
    private LocalDateTime datePayedKeepingNP;

    @Column
    private boolean fullPayment = false;

    @Column
    private String cityRefNP;

    @Column
    private String city;

    @Column
    private boolean available = true;

    @Column
    private boolean withoutTTN;

    @Column
    private LocalDateTime dateCreated;

    @Column
    private boolean notForDeliveryFile;

    @Column
    private Boolean returned = false;

    @Column
    private String returnTtn;

    @Column
    private boolean canceledAfter = false;

    @Column
    private boolean payed = false;

    @Column
    private boolean payedForUser = false;

    @Column
    private Integer sequenceNumber;

    @Column
    private Long npAccountId;

    @Column
    private Boolean urgent;

    public boolean isNotForDeliveryFile() {
        return notForDeliveryFile;
    }

    public void setNotForDeliveryFile(boolean notForDeliveryFile) {
        this.notForDeliveryFile = notForDeliveryFile;
    }

    public List<Shoe> getOrderedShoes() {
        return orderedShoes;
    }

    public void setOrderedShoes(List<Shoe> orderedShoes) {
        this.orderedShoes = orderedShoes;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getPrePayment() {
        return prePayment;
    }

    public void setPrePayment(Double prePayment) {
        this.prePayment = prePayment;
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

    public String getPostComment() {
        return postComment;
    }

    public void setPostComment(String postComment) {
        this.postComment = postComment;
    }

    public LocalDateTime getLastTransactionDateTime() {
        return lastTransactionDateTime;
    }

    public void setLastTransactionDateTime(LocalDateTime lastTransactionDateTime) {
        this.lastTransactionDateTime = lastTransactionDateTime;
    }

    public Double getReturnSumNP() {
        return returnSumNP;
    }

    public void setReturnSumNP(Double returnSumNP) {
        this.returnSumNP = returnSumNP;
    }

    public String getNameAndSurnameNP() {
        return nameAndSurnameNP;
    }

    public void setNameAndSurnameNP(String nameAndSurnameNP) {
        this.nameAndSurnameNP = nameAndSurnameNP;
    }

    public String getLastCreatedOnTheBasisDocumentTypeNP() {
        return lastCreatedOnTheBasisDocumentTypeNP;
    }

    public void setLastCreatedOnTheBasisDocumentTypeNP(String lastCreatedOnTheBasisDocumentTypeNP) {
        this.lastCreatedOnTheBasisDocumentTypeNP = lastCreatedOnTheBasisDocumentTypeNP;
    }

    public LocalDateTime getDatePayedKeepingNP() {
        return datePayedKeepingNP;
    }

    public void setDatePayedKeepingNP(LocalDateTime datePayedKeepingNP) {
        this.datePayedKeepingNP = datePayedKeepingNP;
    }

    public boolean isFullPayment() {
        return fullPayment;
    }

    public void setFullPayment(boolean fullPayment) {
        this.fullPayment = fullPayment;
    }

    public String getCityRefNP() {
        return cityRefNP;
    }

    public void setCityRefNP(String cityRefNP) {
        this.cityRefNP = cityRefNP;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isWithoutTTN() {
        return withoutTTN;
    }

    public void setWithoutTTN(boolean withoutTTN) {
        this.withoutTTN = withoutTTN;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getStatusNP() {
        return statusNP;
    }

    public void setStatusNP(Integer statusNP) {
        this.statusNP = statusNP;
    }

    public Boolean getReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    public String getReturnTtn() {
        return returnTtn;
    }

    public void setReturnTtn(String returnTtn) {
        this.returnTtn = returnTtn;
    }

    public boolean isCanceledAfter() {
        return canceledAfter;
    }

    public void setCanceledAfter(boolean canceledAfter) {
        this.canceledAfter = canceledAfter;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPayedForUser() {
        return payedForUser;
    }

    public void setPayedForUser(boolean payedForUser) {
        this.payedForUser = payedForUser;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }
}
