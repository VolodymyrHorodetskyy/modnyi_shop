package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class CriticalExceptionRecord extends Audit {

    private String description;

    public CriticalExceptionRecord(String description) {
        this.description = description;
    }
}
