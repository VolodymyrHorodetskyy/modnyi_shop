package shop.chobitok.modnyi.entity;

import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class CompanyFinanceControl extends Audit {

    @OneToOne
    private Company company;
    private Double currentFinanceState;
    private Double operation;
    private String description;

    public CompanyFinanceControl() {
    }

    public CompanyFinanceControl(DoCompanyFinanceControlOperationRequest request, Double currentFinanceState, Company company) {
        this.company = company;
        this.currentFinanceState = currentFinanceState;
        this.operation = request.getOperation();
        this.description = request.getDescription();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Double getCurrentFinanceState() {
        return currentFinanceState;
    }

    public void setCurrentFinanceState(Double currentFinanceState) {
        this.currentFinanceState = currentFinanceState;
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
