package shop.chobitok.modnyi.entity.request;

public class SaveAdsSpendsRequest {

    private String start;
    private String end;
    private Double spends;
    private Long spendTypeId;
    private String description;

    public SaveAdsSpendsRequest() {
    }

    public SaveAdsSpendsRequest(String start, String end, Double spends, Long spendTypeId, String description) {
        this.start = start;
        this.end = end;
        this.spends = spends;
        this.spendTypeId = spendTypeId;
        this.description = description;
    }

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

    public Long getSpendTypeId() {
        return spendTypeId;
    }

    public void setSpendTypeId(Long spendTypeId) {
        this.spendTypeId = spendTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
