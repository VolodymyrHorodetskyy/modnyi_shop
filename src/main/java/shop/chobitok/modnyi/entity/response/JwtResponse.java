package shop.chobitok.modnyi.entity.response;

public class JwtResponse {

    private final Long userId;
    private final String jwttoken;

    public JwtResponse(Long userId, String jwttoken) {
        this.userId = userId;
        this.jwttoken = jwttoken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getJwttoken() {
        return jwttoken;
    }
}
