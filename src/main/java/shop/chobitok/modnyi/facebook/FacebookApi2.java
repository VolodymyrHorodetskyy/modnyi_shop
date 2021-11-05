package shop.chobitok.modnyi.facebook;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.Pixel;
import shop.chobitok.modnyi.facebook.entity.FacebookEvent;
import shop.chobitok.modnyi.facebook.helper.FBHelper;
import shop.chobitok.modnyi.service.PixelService;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class FacebookApi2 {

    private PixelService pixelService;

    String url1 = "https://graph.facebook.com/v12.0/";
    String url2 = "/events?access_token=";

    public FacebookApi2(PixelService pixelService) {
        this.pixelService = pixelService;
    }

    public ResponseEntity<Object> send(AppOrder appOrder) {
        if (checkPixel(appOrder.getPixel()) &&
                !isEmpty(appOrder.getFbc()) &&
                !isEmpty(appOrder.getFbp())) {
            return send(appOrder,
                    FBHelper.createFacebookPurchaseEvent(appOrder.getFbp(), appOrder.getFbc(), 1699));
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
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(formUrl(appOrder.getPixel().getPixelId(), appOrder.getPixel().getPixelAccessToken()),
                facebookEvent, Object.class);
    }

    private String formUrl(String pixelId, String accessToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url1)
                .append(pixelId)
                .append(url2)
                .append(accessToken);
        return stringBuilder.toString();
    }
}
