package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class Order {

    @JsonProperty("order_id")
    public Long orderId;
    @JsonProperty("user")
    public Integer user;
    @JsonProperty("delivery_name")
    public String deliveryName;
    @JsonProperty("delivery_email")
    public String deliveryEmail;
    @JsonProperty("delivery_phone")
    public String deliveryPhone;
    @JsonProperty("delivery_city")
    public String deliveryCity;
    @JsonProperty("delivery_city_stable")
    public String deliveryCityStable;
    @JsonProperty("delivery_address")
    public String deliveryAddress;
    @JsonProperty("delivery_type")
    public DeliveryType deliveryType;
    @JsonProperty("delivery_price")
    public Integer deliveryPrice;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("payment_type")
    public PaymentType paymentType;
    @JsonProperty("payment_price")
    public Integer paymentPrice;
    @JsonProperty("payed")
    public Integer payed;
    @JsonProperty("total_default")
    public Integer totalDefault;
    @JsonProperty("total_sum")
    public Integer totalSum;
    @JsonProperty("total_quantity")
    public Integer totalQuantity;
    @JsonProperty("discount_percent")
    public Integer discountPercent;
    @JsonProperty("discount_value")
    public Integer discountValue;
    @JsonProperty("coupon_code")
    public String couponCode;
    @JsonProperty("coupon_percent")
    public Integer couponPercent;
    @JsonProperty("coupon_discount_value")
    public Integer couponDiscountValue;
    @JsonProperty("coupon_type")
    public Integer couponType;
    @JsonProperty("stat_status")
    public Integer statStatus;
    @JsonProperty("stat_created")
    public String statCreated;
    @JsonProperty("currency")
    public String currency;
    @JsonProperty("order_without_callback")
    public Boolean orderWithoutCallback;
    @JsonProperty("manager_id")
    public Integer managerId;
    @JsonProperty("manager_comment")
    public String managerComment;
    @JsonProperty("manager_discount")
    public Integer managerDiscount;
    @JsonProperty("manager_discount_title")
    public String managerDiscountTitle;
    @JsonProperty("products")
    public List<Product> products;
    @JsonProperty("additional_data")
    public List<Object> additionalData;
    @JsonProperty("analytics")
    public Analytics analytics;
    @JsonProperty("delivery_data")
    public JsonNode deliveryData;
}
