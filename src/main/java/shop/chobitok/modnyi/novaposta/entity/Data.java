package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    @JsonProperty("RecipientAddress")
    private String RecipientAddress;
    @JsonProperty("StatusCode")
    private int StatusCode;
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("Redelivery")
    private Integer Redelivery;
    @JsonProperty("RedeliverySum")
    private Integer RedeliverySum;
    @JsonProperty("Number")
    private String Number;

    public String getRecipientAddress() {
        return RecipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        RecipientAddress = recipientAddress;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Integer getRedelivery() {
        return Redelivery;
    }

    public void setRedelivery(Integer redelivery) {
        Redelivery = redelivery;
    }

    public Integer getRedeliverySum() {
        return RedeliverySum;
    }

    public void setRedeliverySum(Integer redeliverySum) {
        RedeliverySum = redeliverySum;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
