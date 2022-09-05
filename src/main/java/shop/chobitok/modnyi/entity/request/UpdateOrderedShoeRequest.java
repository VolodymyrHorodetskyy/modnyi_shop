package shop.chobitok.modnyi.entity.request;

public class UpdateOrderedShoeRequest {

    private Long orderedShoeId;
    private Boolean shouldNotBePayed;

    public Long getOrderedShoeId() {
        return orderedShoeId;
    }

    public void setOrderedShoeId(Long orderedShoeId) {
        this.orderedShoeId = orderedShoeId;
    }

    public Boolean getShouldNotBePayed() {
        return shouldNotBePayed;
    }

    public void setShouldNotBePayed(Boolean shouldNotBePayed) {
        this.shouldNotBePayed = shouldNotBePayed;
    }
}
