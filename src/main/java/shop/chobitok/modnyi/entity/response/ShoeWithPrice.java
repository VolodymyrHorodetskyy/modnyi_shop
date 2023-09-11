package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Variants;

public class ShoeWithPrice extends Shoe {

    private Double cost;
    private Double price;
    private Variants shoeType;

    public ShoeWithPrice(Shoe shoe, Double cost, Double price) {
        super(shoe);
        this.cost = cost;
        this.price = price;
        this.shoeType = shoe.getShoeType();
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public Variants getShoeType() {
        return shoeType;
    }

    @Override
    public void setShoeType(Variants shoeType) {
        this.shoeType = shoeType;
    }
}
