package shop.chobitok.modnyi.service.horoshop.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Analytics {

    @JsonProperty("utm_source")
    public String utmSource;
    @JsonProperty("utm_medium")
    public String utmMedium;
    @JsonProperty("utm_campaign")
    public String utmCampaign;
    @JsonProperty("utm_term")
    public String utmTerm;
    @JsonProperty("utm_content")
    public String utmContent;
    @JsonProperty("google_client_id")
    public String googleClientId;
}