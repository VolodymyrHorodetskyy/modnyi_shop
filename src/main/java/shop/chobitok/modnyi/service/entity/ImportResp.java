package shop.chobitok.modnyi.service.entity;

public class ImportResp {

    private String stringResult;
    private boolean coincidenceFound;
    private String errors;

    public ImportResp(String stringResult, boolean coincidenceFound) {
        this.stringResult = stringResult;
        this.coincidenceFound = coincidenceFound;
    }

    public ImportResp(String stringResult, boolean coincidenceFound, String errors) {
        this.stringResult = stringResult;
        this.coincidenceFound = coincidenceFound;
        this.errors = errors;
    }

    public String getStringResult() {
        return stringResult;
    }

    public void setStringResult(String stringResult) {
        this.stringResult = stringResult;
    }

    public boolean isCoincidenceFound() {
        return coincidenceFound;
    }

    public void setCoincidenceFound(boolean coincidenceFound) {
        this.coincidenceFound = coincidenceFound;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
