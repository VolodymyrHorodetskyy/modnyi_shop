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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.entity.*;
import shop.chobitok.modnyi.novaposta.request.*;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NovaPostaRepository {

    private String getTrackingURL = "https://api.novaposhta.ua/v2.0/json/getStatusDocuments";
    private String getListTrackingURL = "https://api.novaposhta.ua/v2.0/json/getDocumentList";
    private String cargoReturnURL = "https://api.novaposhta.ua/v2.0/json/save";
    private String checkPossibilityReturnCargoURL = "https://api.novaposhta.ua/v2.0/json/CheckPossibilityCreateReturn";

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
        ResponseEntity<TrackingEntity> responseEntity = null;
        //TODO: refactoring
        while (true) {
            try {
                responseEntity = restTemplate.postForEntity(getTrackingURL, httpEntity, TrackingEntity.class);
                break;
            } catch (ResourceAccessException e) {
                System.out.println(getTrackingRequest.getMethodProperties().getDocuments().get(0).getDocumentNumber());
            }
        }
        TrackingEntity trackingEntity = responseEntity.getBody();
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
        ResponseEntity<ListTrackingEntity> responseEntity = restTemplate.postForEntity(getListTrackingURL, httpEntity, ListTrackingEntity.class);
        return responseEntity.getBody();
    }

    public CheckPossibilityCreateReturnResponse checkPossibilitReturn(String ttn) {
        CheckPossibilityReturnCargoRequest checkPossibilityReturnCargoRequest = new CheckPossibilityReturnCargoRequest();
        checkPossibilityReturnCargoRequest.setApiKey(apiKey);
        checkPossibilityReturnCargoRequest.setMethodProperties(new MethodPropertiesForCheckReturn(ttn));
        HttpEntity httpEntity = new HttpEntity(checkPossibilityReturnCargoRequest, httpHeaders);
        ResponseEntity<CheckPossibilityCreateReturnResponse> responseEntity = restTemplate.postForEntity(checkPossibilityReturnCargoURL, httpEntity, CheckPossibilityCreateReturnResponse.class);
        return responseEntity.getBody();
    }

    public boolean returnCargo(ReturnCargoRequest returnCargoRequest) {
        returnCargoRequest.setApiKey(apiKey);
        HttpEntity httpEntity = new HttpEntity(returnCargoRequest, httpHeaders);
        ResponseEntity<CargoReturnResponse> responseEntity = restTemplate.postForEntity(cargoReturnURL, httpEntity, CargoReturnResponse.class);
        return responseEntity.getBody().isSuccess();
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

}
