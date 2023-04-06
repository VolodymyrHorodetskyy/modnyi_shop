package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String token;

    public String getToken() {
        return token;
    }
}
