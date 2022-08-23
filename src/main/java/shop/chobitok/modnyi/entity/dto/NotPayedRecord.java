package shop.chobitok.modnyi.entity.dto;

public class NotPayedRecord {

    private String ttn;
    private String modelAndColor;
    private Double sum;
    private String note;
    private Long orderedShoeId;
    private Long payedRecordId;

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getModelAndColor() {
        return modelAndColor;
    }

    public void setModelAndColor(String modelAndColor) {
        this.modelAndColor = modelAndColor;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrderedShoeId() {
        return orderedShoeId;
    }

    public void setOrderedShoeId(Long orderedShoeId) {
        this.orderedShoeId = orderedShoeId;
    }

    public Long getPayedRecordId() {
        return payedRecordId;
    }

    public void setPayedRecordId(Long payedRecordId) {
        this.payedRecordId = payedRecordId;
    }
}
