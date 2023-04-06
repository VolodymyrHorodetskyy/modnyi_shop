package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    @JsonProperty("title")
    public String title;
    @JsonProperty("article")
    public String article;
    @JsonProperty("price")
    public Integer price;
    @JsonProperty("quantity")
    public Integer quantity;
    @JsonProperty("discount_marker")
    public String discountMarker;
    @JsonProperty("type")
    public String type;
    @JsonProperty("storage_id")
    public Integer storageId;
    @JsonProperty("parent_storage_id")
    public Object parentStorageId;
    @JsonProperty("total_price")
    public Integer totalPrice;

}
