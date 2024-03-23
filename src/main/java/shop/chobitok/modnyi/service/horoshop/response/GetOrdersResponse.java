package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrdersResponse {

    @JsonProperty("status")
    public String status;
    @JsonProperty("response")
    public OrdersResponse response;
    public String site;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrdersResponse getResponse() {
        return response;
    }

    public void setResponse(OrdersResponse response) {
        this.response = response;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
