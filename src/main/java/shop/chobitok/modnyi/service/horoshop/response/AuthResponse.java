package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    public String status;
    public Response response;

    public String getStatus() {
        return status;
    }

    public Response getResponse() {
        return response;
    }
}
