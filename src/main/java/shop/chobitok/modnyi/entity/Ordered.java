package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Ordered extends Audit {

    @ManyToMany
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
    private Double returnSum;

    @Column
    private String nameAndSurnameNP;

    @Column
    private String lastCreatedOnTheBasisDocumentType;

    @Column
    private LocalDateTime datePayedKeeping;


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

    public Double getReturnSum() {
        return returnSum;
    }

    public void setReturnSum(Double returnSum) {
        this.returnSum = returnSum;
    }

    public String getNameAndSurnameNP() {
        return nameAndSurnameNP;
    }

    public void setNameAndSurnameNP(String nameAndSurnameNP) {
        this.nameAndSurnameNP = nameAndSurnameNP;
    }

    public String getLastCreatedOnTheBasisDocumentType() {
        return lastCreatedOnTheBasisDocumentType;
    }

    public void setLastCreatedOnTheBasisDocumentType(String lastCreatedOnTheBasisDocumentType) {
        this.lastCreatedOnTheBasisDocumentType = lastCreatedOnTheBasisDocumentType;
    }

    public LocalDateTime getDatePayedKeeping() {
        return datePayedKeeping;
    }

    public void setDatePayedKeeping(LocalDateTime datePayedKeeping) {
        this.datePayedKeeping = datePayedKeeping;
    }
}
