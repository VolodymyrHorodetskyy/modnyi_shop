package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.Ordered;

public class AddShoeToOrderResponse {

    private Ordered ordered;
    private boolean foundCoincidence;

    public AddShoeToOrderResponse(Ordered ordered, boolean foundCoincidence) {
        this.ordered = ordered;
        this.foundCoincidence = foundCoincidence;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }

    public boolean isFoundCoincidence() {
        return foundCoincidence;
    }

    public void setFoundCoincidence(boolean foundCoincidence) {
        this.foundCoincidence = foundCoincidence;
    }
}
