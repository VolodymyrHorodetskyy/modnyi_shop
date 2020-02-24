package shop.chobitok.modnyi.entity.request;

public class FromTTNFileRequest {

    private String path;


    public FromTTNFileRequest() {
    }

    public FromTTNFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
