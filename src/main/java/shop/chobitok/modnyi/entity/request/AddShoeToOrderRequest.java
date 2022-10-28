package shop.chobitok.modnyi.entity.request;

public class AddShoeToOrderRequest {

    private Long shoeId;
    private Long orderId;
    private int size;
    private String comment;
    private Long storageRecordId;

    public AddShoeToOrderRequest() {
    }

    public AddShoeToOrderRequest(Long orderId, Long shoeId, int size, String comment) {
        this.shoeId = shoeId;
        this.orderId = orderId;
        this.size = size;
        this.comment = comment;
    }

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getStorageRecordId() {
        return storageRecordId;
    }

    public void setStorageRecordId(Long storageRecordId) {
        this.storageRecordId = storageRecordId;
    }
}
