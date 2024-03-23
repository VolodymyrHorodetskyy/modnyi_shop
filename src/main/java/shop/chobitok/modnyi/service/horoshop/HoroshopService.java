package shop.chobitok.modnyi.service.horoshop;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.service.horoshop.request.GetOrdersRequest;
import shop.chobitok.modnyi.service.horoshop.response.AuthResponse;
import shop.chobitok.modnyi.service.horoshop.response.GetOrdersResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static shop.chobitok.modnyi.util.DateHelper.makeDateBeginningOfDay;
import static shop.chobitok.modnyi.util.DateHelper.makeDateEndOfDay;

public class HoroshopService {

    private String authUrl;
    private String ordersUrl;
    private final String login;
    private final String password;
    private final String site;

    private final RestTemplate restTemplate;
    private String token;
    private LocalDateTime tokenExpiration;

    public HoroshopService(RestTemplate restTemplate, HoroshopConfig horoshopConfig) {
        this.restTemplate = restTemplate;
        this.authUrl = horoshopConfig.getAuthUrl();
        this.ordersUrl = horoshopConfig.getOrdersUrl();
        this.login = horoshopConfig.getLogin();
        this.password = horoshopConfig.getPassword();
        this.site = horoshopConfig.getSite();
    }

    public String authenticate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String jsonBody = "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<AuthResponse> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, AuthResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            this.token = Objects.requireNonNull(response.getBody()).getResponse().getToken();
            this.tokenExpiration = LocalDateTime.now().plusMinutes(10);
            return this.token;
        }
        return null;
    }

    private String getToken() {
        if (this.token == null || LocalDateTime.now().isAfter(this.tokenExpiration)) {
            return authenticate();
        }
        return this.token;
    }

    public GetOrdersResponse getOrderData(LocalDateTime from, LocalDateTime to, List<Long> ids) {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GetOrdersRequest request = new GetOrdersRequest.Builder()
                .token(token)
                .ids(ids)
                .from(makeDateBeginningOfDay(from))
                .to(makeDateEndOfDay(to))
                .build();

        HttpEntity entity = new HttpEntity<>(request, headers);

        ResponseEntity<GetOrdersResponse> response = restTemplate.exchange(ordersUrl, HttpMethod.PUT, entity, GetOrdersResponse.class);
        GetOrdersResponse body = response.getBody();
        body.setSite(site);
        return body;
    }
}
