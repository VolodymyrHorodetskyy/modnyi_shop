package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.SpendType;

public class SaveAdsSpends {

    private String start;
    private String end;
    private Double spends;
    private SpendType spendType;
    private String description;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Double getSpends() {
        return spends;
    }

    public void setSpends(Double spends) {
        this.spends = spends;
    }

    public SpendType getSpendType() {
        return spendType;
    }

    public void setSpendType(SpendType spendType) {
        this.spendType = spendType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
