package shop.chobitok.modnyi.entity.response;

import java.time.LocalDateTime;

public class EarningsResponse {

    private LocalDateTime from;
    private LocalDateTime to;
    private Double sum;
    private Double predictedSum;
    private int received;
    private int denied;
    private int all;
    private int receivedPercentage;

    public EarningsResponse() {
    }


    public EarningsResponse(LocalDateTime from, LocalDateTime to, Double sum, Double predictedSum, int received, int denied, int all, int receivedPercentage) {
        this.from = from;
        this.to = to;
        this.sum = sum;
        this.predictedSum = predictedSum;
        this.received = received;
        this.denied = denied;
        this.all = all;
        this.receivedPercentage = receivedPercentage;
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

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getDenied() {
        return denied;
    }

    public void setDenied(int denied) {
        this.denied = denied;
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
