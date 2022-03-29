package shop.chobitok.modnyi.entity;

import shop.chobitok.modnyi.entity.request.AccountingRecordRequest;

import javax.persistence.Entity;

@Entity
public class AccountingRecord extends Audit {

    private Double currentValue;
    private Double operationValue;
    private String description;

    public AccountingRecord(AccountingRecordRequest request, Double currentValue) {
        this.operationValue = request.getOperationValue();
        this.description = request.getDescription();
        this.currentValue = currentValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

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
