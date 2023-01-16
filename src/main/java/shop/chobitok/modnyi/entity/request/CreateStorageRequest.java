package shop.chobitok.modnyi.entity.request;

public class CreateStorageRequest {

    private Long shoeId;
    private Integer size;
    private String comment;

    public CreateStorageRequest() {
    }

    public CreateStorageRequest(Long shoeId, Integer size, String comment) {
        this.shoeId = shoeId;
        this.size = size;
        this.comment = comment;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
