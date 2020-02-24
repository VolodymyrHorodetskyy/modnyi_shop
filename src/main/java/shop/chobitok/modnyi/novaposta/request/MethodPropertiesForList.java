package shop.chobitok.modnyi.novaposta.request;

public class MethodPropertiesForList {
    private String DateTimeFrom;
    private String DateTimeTo;
    private String Page = "1";
    private String GetFullList = "0";


    // Getter Methods

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

    // Setter Methods

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