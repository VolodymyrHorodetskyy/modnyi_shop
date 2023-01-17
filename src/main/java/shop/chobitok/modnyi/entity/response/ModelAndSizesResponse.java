package shop.chobitok.modnyi.entity.response;

public class ModelAndSizesResponse {

    private String modelAndColor;
    private String sizes;

    public ModelAndSizesResponse() {
    }

    public ModelAndSizesResponse(String modelAndColor, String sizes) {
        this.modelAndColor = modelAndColor;
        this.sizes = sizes;
    }

    public String getModelAndColor() {
        return modelAndColor;
    }

    public void setModelAndColor(String modelAndColor) {
        this.modelAndColor = modelAndColor;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }
}
