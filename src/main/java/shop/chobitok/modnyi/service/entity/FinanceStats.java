package shop.chobitok.modnyi.service.entity;

public class FinanceStats {

    private Double earnings;
    private Double projectedEarnings;
    private Double realisticEarning;
    private int receivedPercentage;
    private Double earningsPlusProjected;
    private Double spends;
    private Double earningMinusSpends;
    private Double projectedEarningsMinusSpends;
    private Integer orderedAmount;
    private int monthlyReceivingPercentage;

    public FinanceStats(Double earnings, Double projectedEarnings, int receivedPercentage, Double earningsPlusProjected, Double spends, Double earningMinusSpends, Double projectedEarningsMinusSpends, int monthlyReceivingPercentage) {
        this.earnings = earnings;
        this.projectedEarnings = projectedEarnings;
        this.receivedPercentage = receivedPercentage;
        this.earningsPlusProjected = earningsPlusProjected;
        this.spends = spends;
        this.earningMinusSpends = earningMinusSpends;
        this.projectedEarningsMinusSpends = projectedEarningsMinusSpends;
        this.monthlyReceivingPercentage = monthlyReceivingPercentage;
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

    public Integer getOrderedAmount() {
        return orderedAmount;
    }

    public void setOrderedAmount(Integer orderedAmount) {
        this.orderedAmount = orderedAmount;
    }

    public int getMonthlyReceivingPercentage() {
        return monthlyReceivingPercentage;
    }

    public Double getRealisticEarning() {
        return realisticEarning;
    }

    public void setRealisticEarning(Double realisticEarning) {
        this.realisticEarning = realisticEarning;
    }

    public void setMonthlyReceivingPercentage(int monthlyReceivingPercentage) {
        this.monthlyReceivingPercentage = monthlyReceivingPercentage;
    }
}
