package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class Card extends Audit {

    @Column(unique = true)
    private String cardMask;

    public String getCardMask() {
        return cardMask;
    }

    public void setCardMask(String cardMask) {
        this.cardMask = cardMask;
    }


}
