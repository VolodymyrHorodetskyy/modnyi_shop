package shop.chobitok.modnyi.entity.request;

import java.util.List;

public class UpdatePatternsRequest {

    private Long shoeId;
    private List<String> patterns;

    public Long getShoeId() {
        return shoeId;
    }

    public void setShoeId(Long shoeId) {
        this.shoeId = shoeId;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
