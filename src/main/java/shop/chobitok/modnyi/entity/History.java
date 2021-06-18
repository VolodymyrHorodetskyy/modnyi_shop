package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class History extends Audit {

    private HistoryType type;
    private String record;

    public History() {
    }

    public History(HistoryType type, String record) {
        this.type = type;
        this.record = record;
    }

    public HistoryType getType() {
        return type;
    }

    public void setType(HistoryType type) {
        this.type = type;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
