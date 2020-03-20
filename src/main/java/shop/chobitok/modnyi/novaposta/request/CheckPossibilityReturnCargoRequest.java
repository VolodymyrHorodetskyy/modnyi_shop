package shop.chobitok.modnyi.novaposta.request;

public class CheckPossibilityReturnCargoRequest {

    private String apiKey;
    private String modelName = "AdditionalService";
    private String calledMethod = "CheckPossibilityCreateReturn";

    private MethodPropertiesForCheckReturn methodProperties;

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

    public MethodPropertiesForCheckReturn getMethodProperties() {
        return methodProperties;
    }

    public void setMethodProperties(MethodPropertiesForCheckReturn methodProperties) {
        this.methodProperties = methodProperties;
    }
}
