package shop.chobitok.modnyi.novaposta.entity;

import shop.chobitok.modnyi.entity.Ordered;

public class MarkingResponse {

    private Ordered ordered;
    private String markingUrl;

    public MarkingResponse() {
    }

    public MarkingResponse(Ordered ordered, String markingUrl) {
        this.ordered = ordered;
        this.markingUrl = markingUrl;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }

    public String getMarkingUrl() {
        return markingUrl;
    }

    public void setMarkingUrl(String markingUrl) {
        this.markingUrl = markingUrl;
    }
}
