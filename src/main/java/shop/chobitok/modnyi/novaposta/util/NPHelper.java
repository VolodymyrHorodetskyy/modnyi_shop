package shop.chobitok.modnyi.novaposta.util;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.novaposta.request.*;
import shop.chobitok.modnyi.service.NpAccountService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NPHelper {

    private NpAccountService npAccountService;

    public NPHelper(NpAccountService npAccountService) {
        this.npAccountService = npAccountService;
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

    public GetTrackingRequest formGetTrackingRequest(Long npAccountId, String ttn) {
        return formGetTrackingRequest(npAccountService.getById(npAccountId), Arrays.asList(ttn));
    }

    public GetTrackingRequest formGetTrackingRequest(Long npAccountId, List<String> ttns) {
        return formGetTrackingRequest(npAccountService.getById(npAccountId), ttns);
    }


    public ReturnCargoRequest createReturnCargoRequest(Ordered ordered, String ref) {
        NpAccount npAccount = npAccountService.getByOrder(ordered);
        ReturnCargoRequest returnCargoRequest = new ReturnCargoRequest();
        returnCargoRequest.setApiKey(npAccount.getToken());
        MethodPropertiesForReturn methodPropertiesForReturn = new MethodPropertiesForReturn();
        methodPropertiesForReturn.setIntDocNumber(ordered.getTtn());
        methodPropertiesForReturn.setReturnAddressRef(ref);
        returnCargoRequest.setMethodProperties(methodPropertiesForReturn);
        return returnCargoRequest;
    }
}
