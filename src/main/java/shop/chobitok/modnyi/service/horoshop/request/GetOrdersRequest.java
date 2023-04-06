package shop.chobitok.modnyi.service.horoshop.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class GetOrdersRequest {

    private String token;
    private List<Long> ids;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime to;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public static class Builder {
        private String token;
        private List<Long> ids;
        private LocalDateTime from;
        private LocalDateTime to;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder ids(List<Long> ids) {
            this.ids = ids;
            return this;
        }

        public Builder from(LocalDateTime from) {
            this.from = from;
            return this;
        }

        public Builder to(LocalDateTime to) {
            this.to = to;
            return this;
        }

        public GetOrdersRequest build() {
            GetOrdersRequest request = new GetOrdersRequest();
            request.token = this.token;
            request.ids = this.ids;
            request.from = this.from;
            request.to = this.to;
            return request;
        }
    }
}
