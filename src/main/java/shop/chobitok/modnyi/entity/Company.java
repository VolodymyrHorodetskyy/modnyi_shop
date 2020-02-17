package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Company extends Audit {

    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
