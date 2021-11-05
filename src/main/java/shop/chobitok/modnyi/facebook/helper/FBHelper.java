package shop.chobitok.modnyi.facebook.helper;

import shop.chobitok.modnyi.facebook.entity.Custom_data;
import shop.chobitok.modnyi.facebook.entity.Data;
import shop.chobitok.modnyi.facebook.entity.FacebookEvent;
import shop.chobitok.modnyi.facebook.entity.User_data;

public class FBHelper {

    private static String eventName = "Purchase";

    public static FacebookEvent createFacebookPurchaseEvent(String fbp, String fbc, float sum) {
        FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.setData(createData(eventName,
                createUser_data(fbp, fbc),
                createCustom_data(sum)));
        return facebookEvent;
    }

    private static Data[] createData(String eventName, User_data user_data, Custom_data custom_data) {
        shop.chobitok.modnyi.facebook.entity.Data data = new shop.chobitok.modnyi.facebook.entity.Data();
        data.setEvent_name(eventName);
        data.setEvent_time(System.currentTimeMillis() / 1000L);
        data.setUser_data(user_data);
        data.setCustom_data(custom_data);
        return new Data[]{data};
    }

    private static User_data createUser_data(String fbp, String fbc) {
        User_data user_data = new User_data();
        user_data.setFbp(fbp);
        user_data.setFbc(fbc);
        return user_data;
    }

    private static Custom_data createCustom_data(float sum) {
        Custom_data custom_data = new Custom_data();
        custom_data.setCurrency("UAH");
        custom_data.setValue(sum);
        return custom_data;
    }


}
