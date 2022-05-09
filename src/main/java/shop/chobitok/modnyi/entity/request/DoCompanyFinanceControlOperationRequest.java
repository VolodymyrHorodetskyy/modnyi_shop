package shop.chobitok.modnyi.entity.request;

public class DoCompanyFinanceControlOperationRequest {

    private Long companyId;
    private Double operation;
    private String description;

    public DoCompanyFinanceControlOperationRequest() {
    }

    public DoCompanyFinanceControlOperationRequest(Long companyId, Double operation, String description) {
        this.companyId = companyId;
        this.operation = operation;
        this.description = description;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Double getOperation() {
        return operation;
    }

    public void setOperation(Double operation) {
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
