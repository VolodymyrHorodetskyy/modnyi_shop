package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class Variants extends Audit {

    private String getting;
    private VariantType variantType;
    private int ordering;

    public Variants() {
    }

    public Variants(String getting, VariantType variantType, int ordering) {
        this.getting = getting;
        this.variantType = variantType;
        this.ordering = ordering;
    }

    public String getGetting() {
        return getting;
    }

    public void setGetting(String getting) {
        this.getting = getting;
    }

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(VariantType variantType) {
        this.variantType = variantType;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }
}
