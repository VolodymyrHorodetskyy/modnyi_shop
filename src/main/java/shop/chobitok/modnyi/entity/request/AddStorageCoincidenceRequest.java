package shop.chobitok.modnyi.entity.request;

public class AddStorageCoincidenceRequest {

    private Long orderedShoeId;
    private Long storageRecordId;

    public Long getOrderedShoeId() {
        return orderedShoeId;
    }

    public void setOrderedShoeId(Long orderedShoeId) {
        this.orderedShoeId = orderedShoeId;
    }

    public Long getStorageRecordId() {
        return storageRecordId;
    }

    public void setStorageRecordId(Long storageRecordId) {
        this.storageRecordId = storageRecordId;
    }
}
