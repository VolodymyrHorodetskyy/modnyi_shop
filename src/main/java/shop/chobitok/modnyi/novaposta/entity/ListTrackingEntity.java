package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListTrackingEntity {

    private boolean success;
    private ArrayList<DataForList> data = new ArrayList<DataForList>();

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<DataForList> getData() {
        return data;
    }

    public void setData(ArrayList<DataForList> data) {
        this.data = data;
    }
}

