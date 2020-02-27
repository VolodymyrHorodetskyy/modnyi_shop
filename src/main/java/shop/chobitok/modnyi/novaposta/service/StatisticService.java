package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private NovaPostaRepository postaRepository;
    private NovaPostaService novaPostaService;
    private String isReturnedIdentificator = "CargoReturn";


    public StatisticService(NovaPostaRepository postaRepository, NovaPostaService novaPostaService) {
        this.postaRepository = postaRepository;
        this.novaPostaService = novaPostaService;
    }

    public Object needToBePayed() {
        List<String> payedTTN = ShoeUtil.readTXTFile("C:\\shoe_proj\\payed_ttn");
        List<Ordered> orderedList = novaPostaService.createOrderedFromTTNFile(new FromTTNFileRequest("C:\\shoe_proj\\All_ttn"));
        Set<String> duplicated = new HashSet<>();
        for (Ordered ordered : orderedList) {
            if (duplicated.add(ordered.getTtn()) == false) {
                System.out.println("Duplicate : " + ordered.getTtn());
            }
        }
        duplicated = new HashSet<>();
        for (String s : payedTTN) {
            if (duplicated.add(s) == false) {
                System.out.println("Duplicate : " + s);
            }
        }

        System.out.println("отримані, але не виплачені");
        List<String> strings = ShoeUtil.readTXTFile("C:\\shoe_proj\\All_ttn");
        GetTrackingRequest request = new GetTrackingRequest();
        MethodProperties methodProperties = new MethodProperties();
        List<Document> documents = new ArrayList<>();
        for (String s : strings) {
            Document document = new Document();
            document.setDocumentNumber(s);
            document.setPhone("+380637638967");
            documents.add(document);
        }
        methodProperties.setDocuments(documents);
        request.setMethodProperties(methodProperties);
        TrackingEntity trackingEntity = postaRepository.getTracking(request);
        for (Data data : trackingEntity.getData()) {
            if (ShoeUtil.convertToStatus(Integer.parseInt(data.getStatusCode())) == Status.RECEIVED && !data.getStatusCode().equals("11") && !data.getStatusCode().equals("9")) {
                System.out.println(data.getNumber()+" "+ data.getCargoDescriptionString());
            }
        }
        System.out.println();
        System.out.println("Не оплачені татові");
        orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.RECEIVED && !payedTTN.contains(ordered.getTtn())).collect(Collectors.toList());
        for (Ordered ordered : orderedList) {
            System.out.println(ordered.getTtn() + "  " + ordered.getPostComment() + " " + ordered.getLastTransactionDateTime());
        }


        return null;
    }

    public List<Ordered> getAllDenied(boolean returned) {
        List<Ordered> orderedList = novaPostaService.createOrderedFromTTNFile(new FromTTNFileRequest("C:\\shoe_proj\\All_ttn"));
        Set<String> duplicated = new HashSet<>();
        for (Ordered ordered : orderedList) {
            if (duplicated.add(ordered.getTtn()) == false) {
                System.out.println("Duplicate : " + ordered.getTtn());
            }
        }
        if (returned) {
            orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DENIED && ordered.getLastCreatedOnTheBasisDocumentTypeNP().equals(isReturnedIdentificator)).collect(Collectors.toList());
        } else {
            orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DENIED).collect(Collectors.toList());
        }
        for (Ordered ordered : orderedList) {
            System.out.println(ordered.getTtn() + " " + ordered.getLastCreatedOnTheBasisDocumentTypeNP());
        }
        return orderedList;
    }

    public List<Ordered> getProblematic() {
        List<Ordered> orderedList = novaPostaService.createOrderedFromTTNFile(new FromTTNFileRequest("C:\\shoe_proj\\All_ttn"));
        orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DELIVERED).collect(Collectors.toList());
        for (Ordered ordered : orderedList) {
            System.out.println(ordered.getTtn() + " " + ordered.getDatePayedKeepingNP());
        }
        return orderedList;
    }

    public void showDeniedAndReceived() {
        List<Ordered> orderedList = novaPostaService.createOrderedFromTTNFile(new FromTTNFileRequest("C:\\shoe_proj\\All_ttn"));
        int denied = 0;
        int received = 0;
        for (Ordered ordered : orderedList) {
            if (ordered.getStatus() == Status.RECEIVED) {
                ++received;
            } else if (ordered.getStatus() == Status.DENIED) {
                ++denied;
            }

        }
        System.out.println(received);
        System.out.println(denied);
    }


}
