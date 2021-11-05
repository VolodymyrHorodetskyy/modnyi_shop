package shop.chobitok.modnyi.facebook;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.serverside.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class FacebookApi {

    private String ACCESS_TOKEN = "EAAHdNEEx8IsBADpPC5kv8olZBSkxdkbjKXtZCFZAprZCEbbEXB3YiZBRVjhJJY28oD2JrmdBfdfArZBNZCHpaGnvyCE1WE93ZB0zZAD5zkD9uz4U1SsIQ6ELd5eLkG7IMWEOcZA3pnlzNZA5d3ZCkQUYMLEHcWTYliMuxB1fTQTMov5dQpZAyDdWyx8CaTf3IEbzLfKwZD";
    private String PIXEL_ID = "3092549211069553";

    public void sendEvent() {
        APIContext context = new APIContext(ACCESS_TOKEN, "0a1e9f273b9c742a43b9d84948267cfd",
                "524691478540427").enableDebug(true);
        context.setLogger(System.out);

        UserData userData = new UserData()
                .emails(Arrays.asList("joe@eg.com"))
                .phones(Arrays.asList("12345678901", "14251234567"))
                // It is recommended to send Client IP and User Agent for Conversions API Events.
                .fbc("fb.1.1554763741205.AbCdEfGhIjKlMnOpQrStUvWxYz1234567890")
                .fbp("fb.1.1558571054389.1098115397");

        Event purchaseEvent = new Event();
        purchaseEvent.eventName("Purchase")
                .eventTime(System.currentTimeMillis() / 1000L)
                .userData(userData)
                .eventSourceUrl("http://jaspers-market.com/product/123")
                .actionSource(ActionSource.website);

        EventRequest eventRequest = new EventRequest(PIXEL_ID, context);
 //       eventRequest.setTestEventCode("TEST81516");
        eventRequest.addDataItem(purchaseEvent);

        try {
            EventResponse response = eventRequest.execute();
            System.out.println(String.format("Standard API response : %s ", response));
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

}
