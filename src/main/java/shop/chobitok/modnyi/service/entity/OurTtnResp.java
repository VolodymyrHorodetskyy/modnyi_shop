package shop.chobitok.modnyi.service.entity;

public class OurTtnResp {

    private String all;
    private String payedKeeping;
    private String needAttention;

    public OurTtnResp(String all, String payedKeeping, String needAttention) {
        this.all = all;
        this.payedKeeping = payedKeeping;
        this.needAttention = needAttention;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getPayedKeeping() {
        return payedKeeping;
    }

    public void setPayedKeeping(String payedKeeping) {
        this.payedKeeping = payedKeeping;
    }

    public String getNeedAttention() {
        return needAttention;
    }

    public void setNeedAttention(String needAttention) {
        this.needAttention = needAttention;
    }
}
