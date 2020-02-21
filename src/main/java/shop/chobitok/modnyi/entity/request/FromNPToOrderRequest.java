package shop.chobitok.modnyi.entity.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class FromNPToOrderRequest {

    @NotNull
    private String phone;
    @NotNull
    private String ttn;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }
}
