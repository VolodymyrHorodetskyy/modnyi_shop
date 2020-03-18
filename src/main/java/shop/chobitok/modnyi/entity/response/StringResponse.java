package shop.chobitok.modnyi.entity.response;

import java.util.List;

public class StringResponse {

    private String result;

    private List<String> listResult;

    public StringResponse() {
    }

    public StringResponse(String result) {
        this.result = result;
    }

    public StringResponse(List<String> listResult) {
        this.listResult = listResult;
    }

    public StringResponse(String result, List<String> listResult) {
        this.result = result;
        this.listResult = listResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public List<String> getListResult() {
        return listResult;
    }

    public void setListResult(List<String> listResult) {
        this.listResult = listResult;
    }
}
