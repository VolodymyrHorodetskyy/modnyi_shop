package shop.chobitok.modnyi.facebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.Pixel;
import shop.chobitok.modnyi.facebook.entity.FacebookEvent;

import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.facebook.helper.FBHelper.createFacebookPurchaseEvent;

@Service
public class FacebookApi2 {

    String url1 = "https://graph.facebook.com/v12.0/";
    String url2 = "/events?access_token=";

    public RestResponseDTO send(AppOrder appOrder) {
        if (checkPixel(appOrder.getPixel())) {
            return send(appOrder,
                    createFacebookPurchaseEvent(appOrder.getFbp(), appOrder.getFbc(), appOrder.getValidatedPhones(),
                            appOrder.getMail(), appOrder.getEventSourceUrl(), appOrder.getClientUserAgent(),
                            appOrder.getCityForFb(), appOrder.getFirstNameForFb(), appOrder.getLastNameForFb(),
                            1699));
        } else {
            return new RestResponseDTO("Apporder id = " + appOrder.getId() + " ,domain = " + appOrder.getDomain());
        }
    }

    private boolean checkPixel(Pixel pixel) {
        boolean result = false;
        if (pixel != null && !isEmpty(pixel.getPixelId())
                && !isEmpty(pixel.getPixelAccessToken())
                && pixel.isSendEvents()) {
            result = true;
        }
        return result;
    }

    private RestResponseDTO send(AppOrder appOrder, FacebookEvent facebookEvent) {
        return send(appOrder.getPixel().getPixelId(), appOrder.getPixel().getPixelAccessToken(),
                facebookEvent);
    }

    private RestResponseDTO send(String pixelId, String accessToken, FacebookEvent facebookEvent) {
        RestTemplate restTemplate = new RestTemplate();
        String url = formUrl(pixelId, accessToken);
        String body = null;
        try {
            try {
                body = new ObjectMapper().writeValueAsString(facebookEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ResponseEntity responseEntity = restTemplate.postForEntity(url,
                    facebookEvent, Object.class);
            return new RestResponseDTO(url, body, responseEntity != null ? responseEntity.getStatusCode() : null
                    , responseEntity, null);
        } catch (HttpClientErrorException e) {
            return new RestResponseDTO(url, body, e.getStatusCode(),
                    null, e.getMessage());
        }
    }

    private String formUrl(String pixelId, String accessToken) {
        return url1 +
                pixelId +
                url2 +
                accessToken;
    }
}
