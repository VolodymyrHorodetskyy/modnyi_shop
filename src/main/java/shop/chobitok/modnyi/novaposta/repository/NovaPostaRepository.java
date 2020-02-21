package shop.chobitok.modnyi.novaposta.repository;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;

import java.util.List;

@Service
public class NovaPostaRepository {

    private String getTrackingURL = "https://api.novaposhta.ua/v2.0/json/getStatusDocuments";
    private String key = "6c5e8776a25bc714a36eeac4f70b8b37";

    public TrackingEntity getTracking(GetTrackingRequest getTrackingRequest) {
        getTrackingRequest.setApiKey(key);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(getTrackingRequest, headers);
        ResponseEntity<TrackingEntity> responseEntity = restTemplate.postForEntity(getTrackingURL, httpEntity, TrackingEntity.class);
        TrackingEntity trackingEntity = responseEntity.getBody();
        List<Data> dataList = trackingEntity.getData();
        if (dataList != null && dataList.size() > 0) {
            Data data = dataList.get(0);
            if (StringUtils.isEmpty(data.getRecipientAddress())) {
                throw new ConflictException("Потрібно заповнити телефон");
            }
        }
        return trackingEntity;
    }


    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(createObjectMapper());
        return converter;
    }


    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return objectMapper;
    }

}
