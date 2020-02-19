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
    private String prePayment;


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

    public String getPrePayment() {
        return prePayment;
    }

    public void setPrePayment(String prePayment) {
        this.prePayment = prePayment;
    }
}
