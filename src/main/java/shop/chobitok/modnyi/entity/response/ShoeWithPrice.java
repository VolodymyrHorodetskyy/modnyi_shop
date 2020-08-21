package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.Shoe;

public class ShoeWithPrice extends Shoe {

    private Double cost;
    private Double price;

    public ShoeWithPrice(Shoe shoe, Double cost, Double price) {
        super(shoe);
        this.cost = cost;
        this.price = price;
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
}
