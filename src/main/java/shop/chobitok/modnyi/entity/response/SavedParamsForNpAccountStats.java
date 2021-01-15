package shop.chobitok.modnyi.entity.response;

import java.time.LocalDateTime;

public class SavedParamsForNpAccountStats {

    private Long npAccountId;
    private String from;
    private String to;

    public SavedParamsForNpAccountStats(Long npAccountId, String from, String to) {
        this.npAccountId = npAccountId;
        this.from = from;
        this.to = to;
    }

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
