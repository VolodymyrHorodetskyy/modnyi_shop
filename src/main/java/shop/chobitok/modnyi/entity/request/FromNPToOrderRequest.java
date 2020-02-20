package shop.chobitok.modnyi.entity.request;

import javax.validation.constraints.NotNull;

public class FromNPToOrderRequest {

    @NotNull
    private String phone;
    @NotNull
    private String TTN;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTTN() {
        return TTN;
    }

    public void setTTN(String TTN) {
        this.TTN = TTN;
    }
}
