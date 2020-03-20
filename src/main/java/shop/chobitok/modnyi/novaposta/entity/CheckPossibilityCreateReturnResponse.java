package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckPossibilityCreateReturnResponse {

    private boolean success;
    private List<DataForCheckPossibilityReturn> data = new ArrayList<DataForCheckPossibilityReturn>();
    private List<String> errors = new ArrayList<>();


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataForCheckPossibilityReturn> getData() {
        return data;
    }

    public void setData(List<DataForCheckPossibilityReturn> data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
