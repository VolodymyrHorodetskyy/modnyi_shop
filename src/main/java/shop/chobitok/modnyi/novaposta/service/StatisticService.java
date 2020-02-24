package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private NovaPostaService novaPostaService;
    private String isReturnedIdentificator = "CargoReturn";


    public StatisticService(NovaPostaService novaPostaService) {
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
            orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DENIED && !ordered.getLastCreatedOnTheBasisDocumentType().equals(isReturnedIdentificator)).collect(Collectors.toList());
        } else {
            orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DENIED).collect(Collectors.toList());
        }
        for (Ordered ordered : orderedList) {
            System.out.println(ordered.getTtn() + " " + ordered.getLastCreatedOnTheBasisDocumentType());
        }
        return orderedList;
    }

    public List<Ordered> getProblematic() {
        List<Ordered> orderedList = novaPostaService.createOrderedFromTTNFile(new FromTTNFileRequest("C:\\shoe_proj\\All_ttn"));
        orderedList = orderedList.stream().filter(ordered -> ordered.getStatus() == Status.DELIVERED).collect(Collectors.toList());
        for (Ordered ordered : orderedList) {
            System.out.println(ordered.getTtn() + " " + ordered.getDatePayedKeeping());
        }
        return orderedList;
    }


}
