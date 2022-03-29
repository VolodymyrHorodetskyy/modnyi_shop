package shop.chobitok.modnyi.entity.request;

public class AccountingRecordRequest {

    private Double operationValue;
    private String description;

    public Double getOperationValue() {
        return operationValue;
    }

    public void setOperationValue(Double operationValue) {
        this.operationValue = operationValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
