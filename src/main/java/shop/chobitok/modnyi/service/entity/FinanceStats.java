package shop.chobitok.modnyi.service.entity;

public class FinanceStats {

    private Double earnings;
    private Double projectedEarnings;
    private int receivedPercentage;
    private Double generalEarnings;
    private Double spends;
    private Double cleanEarnings;
    private Double projectedEarningsMinusSpends;

    public FinanceStats(Double earnings, Double projectedEarnings, int receivedPercentage, Double generalEarnings, Double spends, Double cleanEarnings, Double projectedEarningsMinusSpends) {
        this.earnings = earnings;
        this.projectedEarnings = projectedEarnings;
        this.receivedPercentage = receivedPercentage;
        this.generalEarnings = generalEarnings;
        this.spends = spends;
        this.cleanEarnings = cleanEarnings;
        this.projectedEarningsMinusSpends = projectedEarningsMinusSpends;
    }

    public FinanceStats() {
    }

    public Double getEarnings() {
        return earnings;
    }

    public void setEarnings(Double earnings) {
        this.earnings = earnings;
    }

    public Double getProjectedEarnings() {
        return projectedEarnings;
    }

    public void setProjectedEarnings(Double projectedEarnings) {
        this.projectedEarnings = projectedEarnings;
    }

    public Double getGeneralEarnings() {
        return generalEarnings;
    }

    public void setGeneralEarnings(Double generalEarnings) {
        this.generalEarnings = generalEarnings;
    }

    public Double getSpends() {
        return spends;
    }

    public void setSpends(Double spends) {
        this.spends = spends;
    }

    public Double getCleanEarnings() {
        return cleanEarnings;
    }

    public void setCleanEarnings(Double cleanEarnings) {
        this.cleanEarnings = cleanEarnings;
    }

    public int getReceivedPercentage() {
        return receivedPercentage;
    }

    public void setReceivedPercentage(int receivedPercentage) {
        this.receivedPercentage = receivedPercentage;
    }

    public Double getProjectedEarningsMinusSpends() {
        return projectedEarningsMinusSpends;
    }

    public void setProjectedEarningsMinusSpends(Double projectedEarningsMinusSpends) {
        this.projectedEarningsMinusSpends = projectedEarningsMinusSpends;
    }
}
