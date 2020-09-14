package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class User extends Audit {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
