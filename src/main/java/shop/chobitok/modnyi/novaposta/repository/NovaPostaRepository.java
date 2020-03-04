package shop.chobitok.modnyi.novaposta.repository;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.request.*;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NovaPostaRepository {

    private String getTrackingURL = "https://api.novaposhta.ua/v2.0/json/getStatusDocuments";
    private String getListTracking = "https://api.novaposhta.ua/v2.0/json/getDocumentList";

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    @Value("${novaposta.apikey}")
    private String apiKey;

    @Value("${novaposta.phoneNumber}")
    private String phoneNumber;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public TrackingEntity getTracking(GetTrackingRequest getTrackingRequest) {
        getTrackingRequest.setApiKey(apiKey);
        HttpEntity httpEntity = new HttpEntity(getTrackingRequest, httpHeaders);
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

    public ListTrackingEntity getTrackingEntityList(LocalDateTime from, LocalDateTime to) {
        GetDocumentListRequest getDocumentListRequest = new GetDocumentListRequest();
        getDocumentListRequest.setApiKey(apiKey);
        MethodPropertiesForList methodPropertiesForList = new MethodPropertiesForList();
        String fromString = formatDate(from);
        String toString = formatDate(to);
        methodPropertiesForList.setDateTimeFrom(fromString);
        methodPropertiesForList.setDateTimeTo(toString);
        getDocumentListRequest.setMethodProperties(methodPropertiesForList);
        HttpEntity httpEntity = new HttpEntity(getDocumentListRequest, httpHeaders);
        ResponseEntity<ListTrackingEntity> responseEntity = restTemplate.postForEntity(getListTracking, httpEntity, ListTrackingEntity.class);
        return responseEntity.getBody();
    }

    private String formatDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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

    public GetTrackingRequest formGetTrackingRequest(String ttn, String phone, String key) {
        GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
        if (!StringUtils.isEmpty(key)) {
            getTrackingRequest.setApiKey(key);
        } else {
            getTrackingRequest.setApiKey(apiKey);
        }
        List<Document> documentList = new ArrayList<>();
        Document document = new Document();
        document.setDocumentNumber(ttn);
        if (!StringUtils.isEmpty(phone)) {
            document.setPhone(phone);
        } else {
            document.setPhone(phoneNumber);
        }
        documentList.add(document);
        MethodProperties methodProperties = new MethodProperties();
        methodProperties.setDocuments(documentList);
        getTrackingRequest.setMethodProperties(methodProperties);
        return getTrackingRequest;
    }

    public GetTrackingRequest formGetTrackingRequest(String ttn) {
        return formGetTrackingRequest(ttn, null, null);
    }

}
