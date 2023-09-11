package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variants variants = (Variants) o;
        return getting.equals(variants.getting) && variantType == variants.variantType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getting, variantType);
    }
}
