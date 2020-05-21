package shop.chobitok.modnyi.entity.request;

public class AddOrRemovePatternRequest {

    private Long shoeId;
    private String pattern;

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
