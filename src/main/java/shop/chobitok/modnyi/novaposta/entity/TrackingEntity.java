package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TrackingEntity {

    private List<Data> data;


    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }



}
