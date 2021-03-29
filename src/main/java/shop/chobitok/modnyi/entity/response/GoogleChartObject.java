package shop.chobitok.modnyi.entity.response;

import java.util.List;

public class GoogleChartObject {

    List<StringDoubleObj> stringDoubleObjs;

    public GoogleChartObject(List<StringDoubleObj> stringDoubleObjs) {
        this.stringDoubleObjs = stringDoubleObjs;
    }

    public List<StringDoubleObj> getStringDoubleObjs() {
        return stringDoubleObjs;
    }

    public void setStringDoubleObjs(List<StringDoubleObj> stringDoubleObjs) {
        this.stringDoubleObjs = stringDoubleObjs;
    }
}
