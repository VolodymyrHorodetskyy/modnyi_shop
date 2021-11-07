package shop.chobitok.modnyi.facebook;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public ResponseEntity<Object> send(AppOrder appOrder) {
        if (checkPixel(appOrder.getPixel()) &&
                !isEmpty(appOrder.getFbc()) &&
                !isEmpty(appOrder.getFbp())) {
            //TODO process and than send phones and emails
            return send(appOrder,
                    createFacebookPurchaseEvent(appOrder.getFbp(), appOrder.getFbc(), null, null, 1699));
        }
        return null;
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

    public ResponseEntity<Object> send(AppOrder appOrder, FacebookEvent facebookEvent) {
        return send(appOrder.getPixel().getPixelId(), appOrder.getPixel().getPixelAccessToken(),
                facebookEvent);
    }

    public ResponseEntity<Object> send(String pixelId, String accessToken, FacebookEvent facebookEvent) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(formUrl(pixelId, accessToken),
                facebookEvent, Object.class);
    }

    private String formUrl(String pixelId, String accessToken) {
        return url1 +
                pixelId +
                url2 +
                accessToken;
    }
}
