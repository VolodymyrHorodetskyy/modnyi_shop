package shop.chobitok.modnyi.novaposta.repository;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.novaposta.entity.*;
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

    private String getMarkingUrlPart1 = "https://my.novaposhta.ua/orders/printMarking100x100/orders[]/";
    private String getMarkingUrlPart2 = "/type/pdf/apiKey/";

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
            } catch (ResourceAccessException | HttpServerErrorException.GatewayTimeout e) {
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

    public ListTrackingEntity getTrackingEntityList(LocalDateTime from, LocalDateTime to) {
        NpAccount npAccount = propsService.getActual();
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

    public ListTrackingEntity getTrackingEntityList(int daysPeriod) {
        return getTrackingEntityList(LocalDateTime.now().minusDays(daysPeriod), LocalDateTime.now());
    }

    public DataForList getDataForListFromListTrackingEntityInFiveDaysPeriod(String ttn, int daysPeriod) {
        return getDataForList(getTrackingEntityList(daysPeriod), ttn, daysPeriod);
    }

    public DataForList getDataForList(ListTrackingEntity listTrackingEntity, String ttn, int daysPeriod) {
        if (listTrackingEntity == null) {
            listTrackingEntity = getTrackingEntityList(daysPeriod);
        }
        DataForList result = null;
        List<DataForList> list = listTrackingEntity.getData();
        if (list != null && list.size() > 0) {
            result = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ttn)).findFirst().orElse(null);
        }
        return result;
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

    public String getMarking(Ordered ordered) {
        NpAccount npAccount = propsService.getByOrder(ordered);
        StringBuilder link = new StringBuilder();
        link.append(getMarkingUrlPart1).append(ordered.getTtn()).append(getMarkingUrlPart2)
                .append(npAccount.getToken());
        return link.toString();
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
