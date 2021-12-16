package shop.chobitok.modnyi.facebook.helper;

import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.facebook.entity.Custom_data;
import shop.chobitok.modnyi.facebook.entity.Data;
import shop.chobitok.modnyi.facebook.entity.FacebookEvent;
import shop.chobitok.modnyi.facebook.entity.User_data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.ZoneId.systemDefault;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.util.HashingUtil.hashSha256;
import static shop.chobitok.modnyi.util.StringHelper.splitPhonesStringBySemiColonAndValidate;

public class FBHelper {

    private static final String eventName = "Purchase";

    public static FacebookEvent createFacebookPurchaseEventFOR_TEST(String fbp, String fbc, String phones, String email, String eventSourceUrl,
                                                                    String clientUserAgent, String city, String firstName, String lastName, float sum,
                                                                    LocalDateTime eventDateTime, String testCode) {
        if (isEmpty(testCode)) {
            throw new ConflictException("test code must no be empty");
        }
        FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.setData(createData(eventDateTime,
                eventSourceUrl,
                createUser_data(fbp, fbc, hashPhoneArrayList(phones), convertStringToHashedArrayList(email),
                        clientUserAgent, city, firstName, lastName),
                createCustom_data(sum)));
        facebookEvent.setTest_event_code(testCode);
        return facebookEvent;
    }

    public static FacebookEvent createFacebookPurchaseEvent(String fbp, String fbc, String phones, String email, String eventSourceUrl,
                                                            String clientUserAgent, String city, String firstName, String lastName, float sum,
                                                            LocalDateTime eventDateTime) {
        FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.setData(createData(eventDateTime,
                eventSourceUrl,
                createUser_data(fbp, fbc, hashPhoneArrayList(phones), convertStringToHashedArrayList(email),
                        clientUserAgent, city, firstName, lastName),
                createCustom_data(sum)));
        return facebookEvent;
    }

    private static Data[] createData(LocalDateTime eventDateTime, String eventSourceUrl, User_data user_data, Custom_data custom_data) {
        shop.chobitok.modnyi.facebook.entity.Data data = new shop.chobitok.modnyi.facebook.entity.Data();
        data.setEvent_source_url(eventSourceUrl);
        data.setEvent_name(eventName);
        data.setEvent_time(formEventTime(eventDateTime));
        data.setUser_data(user_data);
        data.setCustom_data(custom_data);
        return new Data[]{data};
    }

    private static float formEventTime(LocalDateTime dateTime) {
        return dateTime.atZone(systemDefault()).toInstant().toEpochMilli() / 1000L;
    }

    private static User_data createUser_data(String fbp, String fbc, ArrayList<String> phones, ArrayList<String> emails,
                                             String clientUserAgent, String city, String firstName, String lastName) {
        User_data user_data = new User_data();
        user_data.setEm(emails);
        user_data.setPh(phones);
        user_data.setFbp(fbp);
        user_data.setFbc(fbc);
        user_data.setClient_user_agent(clientUserAgent);
        user_data.setCt(hashSha256(city));
        user_data.setFn(hashSha256(firstName));
        user_data.setLn(hashSha256(lastName));
        user_data.setGe(hashSha256("f"));
        user_data.setCountry(hashSha256("ua"));
        return user_data;
    }

    private static Custom_data createCustom_data(float sum) {
        Custom_data custom_data = new Custom_data();
        custom_data.setCurrency("UAH");
        custom_data.setValue(sum);
        return custom_data;
    }

    private static ArrayList<String> hashDataInStringArrayList(List<String> stringList) {
        ArrayList<String> hashedStringArrayList = new ArrayList<>();
        for (String phone : stringList) {
            hashedStringArrayList.add(hashSha256(phone));
        }
        return hashedStringArrayList;
    }

    private static ArrayList<String> hashPhoneArrayList(String phones) {
        List<String> phonesArrayList = splitPhonesStringBySemiColonAndValidate(phones);
        return hashDataInStringArrayList(phonesArrayList);
    }

    private static ArrayList<String> convertStringToHashedArrayList(String s) {
        if (!isEmpty(s)) {
            return hashDataInStringArrayList(Collections.singletonList(s));
        } else {
            return null;
        }
    }

}
