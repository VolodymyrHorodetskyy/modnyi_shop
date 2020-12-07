package shop.chobitok.modnyi.novaposta.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MethodPropertiesForList {

    private String DateTimeFrom;
    private String DateTimeTo;
    private String Page = "0";
    private String GetFullList = "1";


    public String getDateTimeFrom() {
        return DateTimeFrom;
    }

    public String getDateTimeTo() {
        return DateTimeTo;
    }

    public String getPage() {
        return Page;
    }

    public String getGetFullList() {
        return GetFullList;
    }

    public void setDateTimeFrom(String DateTimeFrom) {
        this.DateTimeFrom = DateTimeFrom;
    }

    public void setDateTimeTo(String DateTimeTo) {
        this.DateTimeTo = DateTimeTo;
    }

    public void setPage(String Page) {
        this.Page = Page;
    }

    public void setGetFullList(String GetFullList) {
        this.GetFullList = GetFullList;
    }
}