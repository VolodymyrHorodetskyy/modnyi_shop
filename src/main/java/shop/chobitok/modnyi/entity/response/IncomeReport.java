package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.DayCosts;

import java.util.ArrayList;
import java.util.List;

public class IncomeReport {

    private double receivedIncome;
    private double receivedIncomeMinusCosts;
    private double potentialIncome;
    private double potentialIncomePlusReceivedIncomeMinusCosts;
    private List<OrderDetail> orderDetailsListReceived;
    private List<OrderDetail> orderDetailsListPotential;
    private List<DayCosts> costsDetailsList;
    private List<String> missingPrices = new ArrayList<>();
    private int approvalPercentage;

    public IncomeReport(double receivedIncome, double receivedIncomeMinusCosts, double potentialIncome, double potentialIncomePlusReceivedIncomeMinusCosts, List<OrderDetail> orderDetailsListReceived, List<OrderDetail> orderDetailsListPotential, List<DayCosts> costsDetailsList, List<String> missingPrices, int approvalPercentage) {
        this.receivedIncome = receivedIncome;
        this.receivedIncomeMinusCosts = receivedIncomeMinusCosts;
        this.potentialIncome = potentialIncome;
        this.potentialIncomePlusReceivedIncomeMinusCosts = potentialIncomePlusReceivedIncomeMinusCosts;
        this.orderDetailsListReceived = orderDetailsListReceived;
        this.orderDetailsListPotential = orderDetailsListPotential;
        this.costsDetailsList = costsDetailsList;
        this.missingPrices = missingPrices;
        this.approvalPercentage = approvalPercentage;
    }

    public IncomeReport() {
    }

    public double getReceivedIncome() {
        return receivedIncome;
    }

    public void setReceivedIncome(double receivedIncome) {
        this.receivedIncome = receivedIncome;
    }

    public double getReceivedIncomeMinusCosts() {
        return receivedIncomeMinusCosts;
    }

    public void setReceivedIncomeMinusCosts(double receivedIncomeMinusCosts) {
        this.receivedIncomeMinusCosts = receivedIncomeMinusCosts;
    }

    public double getPotentialIncome() {
        return potentialIncome;
    }

    public void setPotentialIncome(double potentialIncome) {
        this.potentialIncome = potentialIncome;
    }


    public List<OrderDetail> getOrderDetailsListReceived() {
        return orderDetailsListReceived;
    }

    public void setOrderDetailsListReceived(List<OrderDetail> orderDetailsListReceived) {
        this.orderDetailsListReceived = orderDetailsListReceived;
    }

    public List<OrderDetail> getOrderDetailsListPotential() {
        return orderDetailsListPotential;
    }

    public void setOrderDetailsListPotential(List<OrderDetail> orderDetailsListPotential) {
        this.orderDetailsListPotential = orderDetailsListPotential;
    }

    public List<DayCosts> getCostsDetailsList() {
        return costsDetailsList;
    }

    public void setCostsDetailsList(List<DayCosts> costsDetailsList) {
        this.costsDetailsList = costsDetailsList;
    }

    public List<String> getMissingPrices() {
        return missingPrices;
    }

    public void setMissingPrices(List<String> missingPrices) {
        this.missingPrices = missingPrices;
    }

    public double getPotentialIncomePlusReceivedIncomeMinusCosts() {
        return potentialIncomePlusReceivedIncomeMinusCosts;
    }

    public void setPotentialIncomePlusReceivedIncomeMinusCosts(double potentialIncomePlusReceivedIncomeMinusCosts) {
        this.potentialIncomePlusReceivedIncomeMinusCosts = potentialIncomePlusReceivedIncomeMinusCosts;
    }

    public int getApprovalPercentage() {
        return approvalPercentage;
    }

    public void setApprovalPercentage(int approvalPercentage) {
        this.approvalPercentage = approvalPercentage;
    }
}
