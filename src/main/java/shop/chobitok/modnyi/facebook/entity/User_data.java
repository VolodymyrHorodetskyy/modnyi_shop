package shop.chobitok.modnyi.facebook.entity;

import java.util.ArrayList;

public class User_data {

    private String client_ip_address;
    private String client_user_agent;
    ArrayList<String> em = new ArrayList<>();
    ArrayList<String> ph = new ArrayList<>();
    private String fbc;
    private String fbp;
    private String ge = "f";
    private String country = "ua";
    private String ct;
    private String fn;
    private String ln;

    public String getClient_ip_address() {
        return client_ip_address;
    }

    public void setClient_ip_address(String client_ip_address) {
        this.client_ip_address = client_ip_address;
    }

    public String getClient_user_agent() {
        return client_user_agent;
    }

    public void setClient_user_agent(String client_user_agent) {
        this.client_user_agent = client_user_agent;
    }

    public ArrayList<String> getEm() {
        return em;
    }

    public void setEm(ArrayList<String> em) {
        this.em = em;
    }

    public ArrayList<String> getPh() {
        return ph;
    }

    public void setPh(ArrayList<String> ph) {
        this.ph = ph;
    }

    public String getFbc() {
        return fbc;
    }

    public void setFbc(String fbc) {
        this.fbc = fbc;
    }

    public String getFbp() {
        return fbp;
    }

    public void setFbp(String fbp) {
        this.fbp = fbp;
    }

    public String getGe() {
        return ge;
    }

    public void setGe(String ge) {
        this.ge = ge;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }
}
