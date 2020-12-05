package shop.chobitok.modnyi.novaposta.util;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.novaposta.request.*;
import shop.chobitok.modnyi.service.PropsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NPHelper {

    private PropsService propsService;

    public NPHelper(PropsService propsService) {
        this.propsService = propsService;
    }

    public GetTrackingRequest formGetTrackingRequest(NpAccount npAccount, List<String> ttns) {
        GetTrackingRequest getTrackingRequest = new GetTrackingRequest();
        getTrackingRequest.setApiKey(npAccount.getToken());
        List<Document> documentList = new ArrayList<>();
        for (String ttn : ttns) {
            Document document = new Document();
            document.setDocumentNumber(ttn);
            document.setPhone(npAccount.getPhone());
            documentList.add(document);
        }
        MethodProperties methodProperties = new MethodProperties();
        methodProperties.setDocuments(documentList);
        getTrackingRequest.setMethodProperties(methodProperties);
        return getTrackingRequest;
    }


    public GetTrackingRequest formGetTrackingRequest(Ordered ordered) {
        return formGetTrackingRequest(propsService.getByOrder(ordered), Arrays.asList(ordered.getTtn()));
    }

    public GetTrackingRequest formGetTrackingRequest(Long npAccountId, String ttn) {
        return formGetTrackingRequest(propsService.getById(npAccountId), Arrays.asList(ttn));
    }

    public GetTrackingRequest formGetTrackingRequest(Long npAccountId, List<String> ttns) {
        return formGetTrackingRequest(propsService.getById(npAccountId), ttns);
    }


    public ReturnCargoRequest createReturnCargoRequest(Ordered ordered, String ref) {
        NpAccount npAccount = propsService.getByOrder(ordered);
        ReturnCargoRequest returnCargoRequest = new ReturnCargoRequest();
        returnCargoRequest.setApiKey(npAccount.getToken());
        MethodPropertiesForReturn methodPropertiesForReturn = new MethodPropertiesForReturn();
        methodPropertiesForReturn.setIntDocNumber(ordered.getTtn());
        methodPropertiesForReturn.setReturnAddressRef(ref);
        returnCargoRequest.setMethodProperties(methodPropertiesForReturn);
        return returnCargoRequest;
    }
}
