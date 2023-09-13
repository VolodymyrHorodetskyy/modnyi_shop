package shop.chobitok.modnyi.entity.response;

public class OrderDetail {
    private String ttn;
    private String modelAndColor;
    private double price;
    private double cost;



    public OrderDetail() {
    }

    public OrderDetail(String ttn, String modelAndColor, double price, double cost) {
        this.ttn = ttn;
        this.modelAndColor = modelAndColor;
        this.price = price;
        this.cost = cost;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getModelAndColor() {
        return modelAndColor;
    }

    public void setModelAndColor(String modelAndColor) {
        this.modelAndColor = modelAndColor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
