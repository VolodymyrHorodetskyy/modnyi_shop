package shop.chobitok.modnyi.novaposta.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MethodPropertiesForCheckReturn {

    private String Number;

    public MethodPropertiesForCheckReturn() {
    }

    public MethodPropertiesForCheckReturn(String number) {
        this.Number = number;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        this.Number = number;
    }
}
