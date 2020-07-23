package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.AppOrder;

public class ChangeAppOrderResponse {

    private String message;
    private AppOrder appOrder;

    public ChangeAppOrderResponse(String message, AppOrder appOrder) {
        this.message = message;
        this.appOrder = appOrder;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppOrder getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(AppOrder appOrder) {
        this.appOrder = appOrder;
    }
}
