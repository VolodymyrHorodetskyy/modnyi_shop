package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Params extends Audit {

    @Column
    private String clue;
    @Column
    private String getting;

    public Params() {
    }

    public Params(String clue, String getting) {
        this.clue = clue;
        this.getting = getting;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public String getGetting() {
        return getting;
    }

    public void setGetting(String getting) {
        this.getting = getting;
    }
}
