package shop.chobitok.modnyi.facebook.entity;

public class Data {

    private String event_name;
    private float event_time;
    private String event_id;
    private String event_source_url;
    User_data user_data;
    Custom_data custom_data;
    private boolean opt_out;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public float getEvent_time() {
        return event_time;
    }

    public void setEvent_time(float event_time) {
        this.event_time = event_time;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_source_url() {
        return event_source_url;
    }

    public void setEvent_source_url(String event_source_url) {
        this.event_source_url = event_source_url;
    }

    public User_data getUser_data() {
        return user_data;
    }

    public void setUser_data(User_data user_data) {
        this.user_data = user_data;
    }

    public boolean isOpt_out() {
        return opt_out;
    }

    public void setOpt_out(boolean opt_out) {
        this.opt_out = opt_out;
    }

    public Custom_data getCustom_data() {
        return custom_data;
    }

    public void setCustom_data(Custom_data custom_data) {
        this.custom_data = custom_data;
    }
}
