package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Discount extends Audit {

    @Column
    private String name;
    @Column
    private Integer shoeNumber;
    @Column
    private Integer discountPercentage;
    @Column
    private Boolean main;

    public Discount(String name, Integer shoeNumber, Integer discountPercentage, Boolean main) {
        this.name = name;
        this.shoeNumber = shoeNumber;
        this.discountPercentage = discountPercentage;
        this.main = main;
    }

    public Discount() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShoeNumber() {
        return shoeNumber;
    }

    public void setShoeNumber(Integer shoeNumber) {
        this.shoeNumber = shoeNumber;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }
}
