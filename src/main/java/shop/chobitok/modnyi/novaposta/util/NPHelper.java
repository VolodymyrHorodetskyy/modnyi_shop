package shop.chobitok.modnyi.novaposta.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.novaposta.request.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class NPHelper {

    @Value("${novaposta.apikey}")
    private String apiKey;

    @Value("${novaposta.phoneNumber}")
    private String phoneNumber;

    public GetTrackingRequest formGetTrackingRequest(String ttn, String phone, String key) {
        GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
        if (!StringUtils.isEmpty(key)) {
            getTrackingRequest.setApiKey(key);
        } else {
            getTrackingRequest.setApiKey(apiKey);
        }
        List<Document> documentList = new ArrayList<>();
        Document document = new Document();
        document.setDocumentNumber(ttn);
        if (!StringUtils.isEmpty(phone)) {
            document.setPhone(phone);
        } else {
            document.setPhone(phoneNumber);
        }
        documentList.add(document);
        MethodProperties methodProperties = new MethodProperties();
        methodProperties.setDocuments(documentList);
        getTrackingRequest.setMethodProperties(methodProperties);
        return getTrackingRequest;
    }

    public GetTrackingRequest formGetTrackingRequest(String ttn) {
        return formGetTrackingRequest(ttn, null, null);
    }

    public ReturnCargoRequest createReturnCargoRequest(String ttn) {
        ReturnCargoRequest returnCargoRequest = new ReturnCargoRequest();
        MethodPropertiesForReturn methodPropertiesForReturn = new MethodPropertiesForReturn();
        methodPropertiesForReturn.setIntDocNumber(ttn);
        returnCargoRequest.setMethodProperties(methodPropertiesForReturn);
        return returnCargoRequest;
    }
}
