package shop.chobitok.modnyi.service.horoshop;

public class HoroshopConfig {
    private String authUrl;
    private String ordersUrl;
    private String login;
    private String password;
    private String site;


    public HoroshopConfig(String authUrl, String ordersUrl, String login, String password, String site) {
        this.authUrl = authUrl;
        this.ordersUrl = ordersUrl;
        this.login = login;
        this.password = password;
        this.site = site;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getOrdersUrl() {
        return ordersUrl;
    }

    public void setOrdersUrl(String ordersUrl) {
        this.ordersUrl = ordersUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}

