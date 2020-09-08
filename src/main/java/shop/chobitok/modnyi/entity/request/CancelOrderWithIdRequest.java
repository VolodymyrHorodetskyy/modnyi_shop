package shop.chobitok.modnyi.entity.request;

public class CancelOrderWithIdRequest extends CancelOrderRequest {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
