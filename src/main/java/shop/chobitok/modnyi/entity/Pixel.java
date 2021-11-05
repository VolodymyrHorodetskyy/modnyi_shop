package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;

@Entity
public class Pixel extends Audit {

    private String pixelId;
    private String pixelAccessToken;
    private String accName;
    private boolean sendEvents;

    public Pixel() {
    }

    public Pixel(String pixelId, String pixelAccessToken, String accName, boolean sendEvents) {
        this.pixelId = pixelId;
        this.pixelAccessToken = pixelAccessToken;
        this.accName = accName;
        this.sendEvents = sendEvents;
    }

    public String getPixelId() {
        return pixelId;
    }

    public void setPixelId(String pixelId) {
        this.pixelId = pixelId;
    }

    public boolean isSendEvents() {
        return sendEvents;
    }

    public void setSendEvents(boolean sendEvents) {
        this.sendEvents = sendEvents;
    }

    public String getPixelAccessToken() {
        return pixelAccessToken;
    }

    public void setPixelAccessToken(String pixelAccessToken) {
        this.pixelAccessToken = pixelAccessToken;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }
}
