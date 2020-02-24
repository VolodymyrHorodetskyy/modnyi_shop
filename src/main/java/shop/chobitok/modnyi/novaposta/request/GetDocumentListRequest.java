package shop.chobitok.modnyi.novaposta.request;

public class GetDocumentListRequest {

    private String apiKey;
    private String modelName = "InternetDocument";
    private String calledMethod = "getDocumentList";
    private MethodPropertiesForList methodPropertiesForListObject;

    // Getter Methods

    public String getApiKey() {
        return apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCalledMethod() {
        return calledMethod;
    }

    public MethodPropertiesForList getMethodProperties() {
        return methodPropertiesForListObject;
    }

    // Setter Methods

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setCalledMethod(String calledMethod) {
        this.calledMethod = calledMethod;
    }

    public void setMethodProperties(MethodPropertiesForList methodPropertiesForListObject) {
        this.methodPropertiesForListObject = methodPropertiesForListObject;
    }
}


