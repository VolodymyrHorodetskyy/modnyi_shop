package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.Status;

import java.time.LocalDateTime;
import java.util.Map;

public class EarningsResponse {

    private LocalDateTime from;
    private LocalDateTime to;
    private Double sum;
    private Double predictedSum;
    private Double realisticSum;
    private Map<Status, Integer> amountByStatus;
    private int receivedPercentage;
    private int all;

    public EarningsResponse() {
    }

    public EarningsResponse(Double sum, Double predictedSum, Double realisticSum) {
        this.sum = sum;
        this.predictedSum = predictedSum;
        this.realisticSum = realisticSum;
    }

    public EarningsResponse(LocalDateTime from, LocalDateTime to, Double sum, Double predictedSum, Double realisticSum, Map<Status, Integer> amountByStatus, int receivedPercentage, int all) {
        this.from = from;
        this.to = to;
        this.sum = sum;
        this.predictedSum = predictedSum;
        this.realisticSum = realisticSum;
        this.amountByStatus = amountByStatus;
        this.receivedPercentage = receivedPercentage;
        this.all = all;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getPredictedSum() {
        return predictedSum;
    }

    public void setPredictedSum(Double predictedSum) {
        this.predictedSum = predictedSum;
    }

    public Double getRealisticSum() {
        return realisticSum;
    }

    public void setRealisticSum(Double realisticSum) {
        this.realisticSum = realisticSum;
    }

    public Map<Status, Integer> getAmountByStatus() {
        return amountByStatus;
    }

    public void setAmountByStatus(Map<Status, Integer> amountByStatus) {
        this.amountByStatus = amountByStatus;
    }

    public int getReceivedPercentage() {
        return receivedPercentage;
    }

    public void setReceivedPercentage(int receivedPercentage) {
        this.receivedPercentage = receivedPercentage;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }
}
