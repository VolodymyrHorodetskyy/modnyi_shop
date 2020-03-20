package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Ordered extends Audit {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Shoe> orderedShoes;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

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
    private Double price;

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
    private boolean fromStorage;

    @Column
    private String fromTTN;

    @Column
    private LocalDateTime dateCreated;

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

    public boolean isFromStorage() {
        return fromStorage;
    }

    public void setFromStorage(boolean fromStorage) {
        this.fromStorage = fromStorage;
    }

    public String getFromTTN() {
        return fromTTN;
    }

    public void setFromTTN(String fromTTN) {
        this.fromTTN = fromTTN;
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
}
