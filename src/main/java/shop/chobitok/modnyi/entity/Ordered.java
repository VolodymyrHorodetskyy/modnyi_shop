package shop.chobitok.modnyi.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ordered extends Audit {

    @ManyToMany
    private List<Shoe> orderedShoes;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

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
}
