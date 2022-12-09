package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.AppOrder;

public class ChangeAppOrderResponse {

    private String message;
    private AppOrder appOrder;
    private boolean coincidenceFound;

    public ChangeAppOrderResponse(String message, AppOrder appOrder, boolean coincidenceFound) {
        this.message = message;
        this.appOrder = appOrder;
        this.coincidenceFound = coincidenceFound;
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
