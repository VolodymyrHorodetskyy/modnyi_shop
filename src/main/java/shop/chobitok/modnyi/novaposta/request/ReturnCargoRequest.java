package shop.chobitok.modnyi.novaposta.request;

public class ReturnCargoRequest {

    private String apiKey;
    private String modelName = "AdditionalService";
    private String calledMethod = "save";
    private MethodPropertiesForReturn methodProperties;


    public String getApiKey() {
        return apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCalledMethod() {
        return calledMethod;
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

    public MethodPropertiesForReturn getMethodProperties() {
        return methodProperties;
    }

    public void setMethodProperties(MethodPropertiesForReturn methodProperties) {
        this.methodProperties = methodProperties;
    }
}
