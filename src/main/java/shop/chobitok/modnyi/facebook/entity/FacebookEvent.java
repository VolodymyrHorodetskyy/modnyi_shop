package shop.chobitok.modnyi.facebook.entity;

public class FacebookEvent {

    Data[] data;
    String test_event_code;

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public String getTest_event_code() {
        return test_event_code;
    }

    public void setTest_event_code(String test_event_code) {
        this.test_event_code = test_event_code;
    }
}
