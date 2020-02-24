package shop.chobitok.modnyi.novaposta.dto;

import shop.chobitok.modnyi.entity.Status;

public class ListTrackingEntityDto {

    private String TTN;
    private String recipient;
    private String shoe;
    private Status status;

    public String getTTN() {
        return TTN;
    }

    public void setTTN(String TTN) {
        this.TTN = TTN;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getShoe() {
        return shoe;
    }

    public void setShoe(String shoe) {
        this.shoe = shoe;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
