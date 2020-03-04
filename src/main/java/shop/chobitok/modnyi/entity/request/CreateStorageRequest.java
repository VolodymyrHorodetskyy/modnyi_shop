package shop.chobitok.modnyi.entity.request;

public class CreateStorageRequest {

    private Long shoe;
    private Integer size;
    private String ttn;

    public Long getShoe() {
        return shoe;
    }

    public void setShoe(Long shoe) {
        this.shoe = shoe;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

}
