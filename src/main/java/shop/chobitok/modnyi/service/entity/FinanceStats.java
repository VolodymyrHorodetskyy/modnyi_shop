package shop.chobitok.modnyi.service.entity;

public class FinanceStats {

    private Double earnings;
    private Double projectedEarnings;
    private int receivedPercentage;
    private Double earningsPlusProjected;
    private Double spends;
    private Double earningMinusSpends;
    private Double projectedEarningsMinusSpends;

    public FinanceStats(Double earnings, Double projectedEarnings, int receivedPercentage, Double earningsPlusProjected, Double spends, Double earningMinusSpends, Double projectedEarningsMinusSpends) {
        this.earnings = earnings;
        this.projectedEarnings = projectedEarnings;
        this.receivedPercentage = receivedPercentage;
        this.earningsPlusProjected = earningsPlusProjected;
        this.spends = spends;
        this.earningMinusSpends = earningMinusSpends;
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

    public Double getEarningsPlusProjected() {
        return earningsPlusProjected;
    }

    public void setEarningsPlusProjected(Double earningsPlusProjected) {
        this.earningsPlusProjected = earningsPlusProjected;
    }

    public Double getSpends() {
        return spends;
    }

    public void setSpends(Double spends) {
        this.spends = spends;
    }

    public Double getEarningMinusSpends() {
        return earningMinusSpends;
    }

    public void setEarningMinusSpends(Double earningMinusSpends) {
        this.earningMinusSpends = earningMinusSpends;
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
