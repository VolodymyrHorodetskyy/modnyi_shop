package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Marking extends Audit {

    @ManyToOne
    private Ordered ordered;
    @Column
    private String ttnPrintUrl;
    @Column
    private boolean printed;

    public Marking(Ordered ordered, String ttnPrintUrl) {
        this.ordered = ordered;
        this.ttnPrintUrl = ttnPrintUrl;
    }

    public Marking() {
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }

    public String getTtnPrintUrl() {
        return ttnPrintUrl;
    }

    public void setTtnPrintUrl(String ttnPrintUrl) {
        this.ttnPrintUrl = ttnPrintUrl;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }
}
