package shop.chobitok.modnyi.service.entity;

import shop.chobitok.modnyi.entity.dto.NotPayedRecord;

import java.util.List;

public class NeedToBePayedResponse {

    private String response;
    private List<NotPayedRecord> notPayedTableRecordList;
    private Double generalSum;

    public NeedToBePayedResponse(String response, List<NotPayedRecord> notPayedTableRecordList, Double generalSum) {
        this.response = response;
        this.notPayedTableRecordList = notPayedTableRecordList;
        this.generalSum = generalSum;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<NotPayedRecord> getNotPayedTableRecordList() {
        return notPayedTableRecordList;
    }

    public void setNotPayedTableRecordList(List<NotPayedRecord> notPayedTableRecordList) {
        this.notPayedTableRecordList = notPayedTableRecordList;
    }

    public Double getGeneralSum() {
        return generalSum;
    }

    public void setGeneralSum(Double generalSum) {
        this.generalSum = generalSum;
    }
}
