package shop.chobitok.modnyi.novaposta.repository;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.novaposta.entity.CargoReturnResponse;
import shop.chobitok.modnyi.novaposta.entity.CheckPossibilityCreateReturnResponse;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.request.*;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.service.PropsService;

import javax.annotation.PostConstruct;
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

    private PropsService propsService;
    private NPHelper npHelper;
    private OrderRepository orderRepository;

    public NovaPostaRepository(PropsService propsService, NPHelper npHelper, OrderRepository orderRepository) {
        this.propsService = propsService;
        this.npHelper = npHelper;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    private TrackingEntity receiveTracking(GetTrackingRequest getTrackingRequest) {
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

    public TrackingEntity getTracking(Ordered ordered) {
        return receiveTracking(npHelper.formGetTrackingRequest(ordered.getNpAccountId(), ordered.getTtn()));
    }

    public TrackingEntity getTracking(Long npAccountId, String ttn) {
        return receiveTracking(npHelper.formGetTrackingRequest(npAccountId, ttn));
    }

    public TrackingEntity getTrackingByTtns(Long npAccountId, List<String> ttns) {
        return receiveTracking(npHelper.formGetTrackingRequest(npAccountId, ttns));
    }

    public ListTrackingEntity getTrackingEntityList(Ordered ordered, LocalDateTime from, LocalDateTime to) {
        NpAccount npAccount = propsService.getByOrder(ordered);
        GetDocumentListRequest getDocumentListRequest = new GetDocumentListRequest();
        getDocumentListRequest.setApiKey(npAccount.getToken());
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


    public CheckPossibilityCreateReturnResponse checkPossibilityReturn(Ordered ordered) {
        NpAccount npAccount = propsService.getByOrder(ordered);
        CheckPossibilityReturnCargoRequest checkPossibilityReturnCargoRequest = new CheckPossibilityReturnCargoRequest();
        checkPossibilityReturnCargoRequest.setApiKey(npAccount.getToken());
        checkPossibilityReturnCargoRequest.setMethodProperties(new MethodPropertiesForCheckReturn(ordered.getTtn()));
        HttpEntity httpEntity = new HttpEntity(checkPossibilityReturnCargoRequest, httpHeaders);
        ResponseEntity<CheckPossibilityCreateReturnResponse> responseEntity = restTemplate.postForEntity(checkPossibilityReturnCargoURL, httpEntity, CheckPossibilityCreateReturnResponse.class);
        return responseEntity.getBody();
    }

    public boolean returnCargo(ReturnCargoRequest returnCargoRequest) {
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
