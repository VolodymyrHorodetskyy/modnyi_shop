package shop.chobitok.modnyi.entity.request;

import shop.chobitok.modnyi.entity.AppOrderStatus;

public class ChangeAppOrderStatusAndCommentRequest {

    private Long id;
    private String comment;
    private AppOrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppOrderStatus getStatus() {
        return status;
    }

    public void setStatus(AppOrderStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
