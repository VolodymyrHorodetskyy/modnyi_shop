package shop.chobitok.modnyi.novaposta.request;

public class GetTrackingRequest {

    private String apiKey;
    private String modelName = "TrackingDocument";
    private String calledMethod = "getStatusDocuments";
    private MethodProperties methodProperties;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCalledMethod() {
        return calledMethod;
    }

    public void setCalledMethod(String calledMethod) {
        this.calledMethod = calledMethod;
    }

    public MethodProperties getMethodProperties() {
        return methodProperties;
    }

    public void setMethodProperties(MethodProperties methodProperties) {
        this.methodProperties = methodProperties;
    }


}
