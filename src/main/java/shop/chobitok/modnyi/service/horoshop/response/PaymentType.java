package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentType {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("title")
    public String title;

}
