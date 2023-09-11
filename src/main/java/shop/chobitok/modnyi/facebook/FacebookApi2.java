package shop.chobitok.modnyi.facebook;

import com.facebook.ads.sdk.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.Pixel;
import shop.chobitok.modnyi.facebook.entity.FacebookEvent;

import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.facebook.helper.FBHelper.createFacebookPurchaseEvent;
import static shop.chobitok.modnyi.facebook.helper.FBHelper.createFacebookPurchaseEventFOR_TEST;

@Service
public class FacebookApi2 {

    String url1 = "https://graph.facebook.com/v15.0/";
    String url2 = "/events?access_token=";

    public RestResponseDTO send(String testCode, AppOrder appOrder) {
        if (checkPixel(appOrder.getPixel())) {
            FacebookEvent facebookEvent;
            if (isEmpty(testCode)) {
                facebookEvent = createFacebookPurchaseEvent(appOrder.getFbp(), appOrder.getFbc(), appOrder.getValidatedPhones(),
                        appOrder.getMail(), appOrder.getEventSourceUrl(), appOrder.getClientUserAgent(),
                        appOrder.getCityForFb(), appOrder.getFirstNameForFb(), appOrder.getLastNameForFb(),
                        1699, appOrder.getCreatedDate());
            } else {
                facebookEvent = createFacebookPurchaseEventFOR_TEST(appOrder.getFbp(), appOrder.getFbc(), appOrder.getValidatedPhones(),
                        appOrder.getMail(), appOrder.getEventSourceUrl(), appOrder.getClientUserAgent(),
                        appOrder.getCityForFb(), appOrder.getFirstNameForFb(), appOrder.getLastNameForFb(),
                        1699, appOrder.getCreatedDate(), testCode);
            }
            return send(appOrder,
                    facebookEvent);
        } else {
            Long pixelId = null;
            if (appOrder.getPixel() != null) {
                pixelId = appOrder.getPixel().getId();
            }
            return new RestResponseDTO("Apporder id = " + appOrder.getId() + " ,domain = " + appOrder.getDomain()
                    + " ,pixel = " + pixelId);
        }
    }

    public RestResponseDTO send(AppOrder appOrder) {
        return send(null, appOrder);
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
        } catch (HttpClientErrorException | ResourceAccessException e) {
            return new RestResponseDTO(url, body, null,
                    null, e.getMessage());
        }
    }

    private String formUrl(String pixelId, String accessToken) {
        return url1 +
                pixelId +
                url2 +
                accessToken;
    }

    public String getCampaignInfo(
            String startDate,
            String endDate) {

        String accessToken = "EABa5j4XJ44oBAJNGTZAW0pum4W4auC7e58BAci4dbPZBcb1g6kbvrNA0kNpaFwRYZBcCIsKfQy2ONezgAn5gJVbSpeQiuMScJ9c670HstQWZAutuh0eEMwGsZAompdXmIwHhDIvMBYWFFMhYz3FBDPtczMWFPbsHEfmMt2kvBY7ZAu0Ub1A3ZBiBjeJsFv1Ri0yHybtwDB9EiSWZCbmGZCOEOClG6fJAxaCcZD";
        APIContext context = new APIContext(accessToken).enableDebug(true);

        AdAccount account = new AdAccount("1343124976510448", context);
        APINodeList<Campaign> campaigns = null;
        try {
            campaigns = account.getCampaigns()
                    .requestAllFields()
                    .execute();
        } catch (APIException e) {
            e.printStackTrace();
        }

        double totalSpend = 0.0;
        for (Campaign campaign : campaigns) {
            APINodeList<AdSet> adSets = null;
            try {
                adSets = campaign.getAdSets()
                    //    .request()
                        .execute();
            } catch (APIException e) {
                e.printStackTrace();
            }

            for (AdSet adSet : adSets) {
                System.out.println("here");

                /*
                campaign.getAdSets().requestField("COST_CAP").execute().get(0).getInsights().execute()
                AdSet.Statistics stats = adSet.getStats()
                        .setTimeRange("{\"since\":\"" + startDate + "\",\"until\":\"" + endDate + "\"}")
                        .requestAllFields()
                        .execute();

                double spend = stats.getSpend();
                double costPerResult = stats.getCostPerResult();*/
                // Do something with the spend and costPerResult values, such as adding them to a total.

            }
        }

        // Return the total spend and cost per result as the response.
    //    String response = String.format("Total spend: %.2f, Cost per result: %.2f", totalSpend, costPerResult);
       // return ResponseEntity.ok(response);
        return null;
    }
}
