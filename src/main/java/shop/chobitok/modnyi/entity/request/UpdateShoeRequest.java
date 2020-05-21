package shop.chobitok.modnyi.entity.request;

public class UpdateShoeRequest extends CreateShoeRequest{

    private Long shoeId;

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }
}
