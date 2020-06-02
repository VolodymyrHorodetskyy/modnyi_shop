package shop.chobitok.modnyi.service.entity;

import shop.chobitok.modnyi.entity.Shoe;

public class StatShoe {

    private Shoe shoe;
    private Integer receivedAmount;
    private Integer deniedAmount;
    private Integer receivedPercentage;
    private Integer generalAmount;

    public StatShoe() {
    }

    public StatShoe(Shoe shoe, Integer receivedAmount, Integer deniedAmount, Integer receivedPercentage, Integer generalAmount) {
        this.shoe = shoe;
        this.receivedAmount = receivedAmount;
        this.deniedAmount = deniedAmount;
        this.receivedPercentage = receivedPercentage;
        this.generalAmount = generalAmount;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public Integer getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(Integer receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public Integer getDeniedAmount() {
        return deniedAmount;
    }

    public void setDeniedAmount(Integer deniedAmount) {
        this.deniedAmount = deniedAmount;
    }

    public Integer getReceivedPercentage() {
        return receivedPercentage;
    }

    public void setReceivedPercentage(Integer receivedPercentage) {
        this.receivedPercentage = receivedPercentage;
    }

    public Integer getGeneralAmount() {
        return generalAmount;
    }

    public void setGeneralAmount(Integer generalAmount) {
        this.generalAmount = generalAmount;
    }
}
