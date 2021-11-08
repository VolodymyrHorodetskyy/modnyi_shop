package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class SendEventsHistory extends Audit {

    private String url;
    private String body;
    private int httpStatus;
    private String message;
    @OneToOne
    private AppOrderToPixel appOrderToPixel;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppOrderToPixel getAppOrderToPixel() {
        return appOrderToPixel;
    }

    public void setAppOrderToPixel(AppOrderToPixel appOrderToPixel) {
        this.appOrderToPixel = appOrderToPixel;
    }
}
