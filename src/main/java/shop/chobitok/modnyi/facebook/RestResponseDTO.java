package shop.chobitok.modnyi.facebook;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestResponseDTO {
    private String url;
    private String body;
    private HttpStatus httpStatus;
    private ResponseEntity responseEntity;
    private String message;


    public RestResponseDTO(String message) {
        this.message = message;
    }

    public RestResponseDTO(String url, String body, HttpStatus httpStatus, ResponseEntity responseEntity, String message) {
        this.url = url;
        this.body = body;
        this.httpStatus = httpStatus;
        this.responseEntity = responseEntity;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ResponseEntity getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity responseEntity) {
        this.responseEntity = responseEntity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
