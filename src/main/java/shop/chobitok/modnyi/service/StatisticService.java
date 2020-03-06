package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.request.Document;
import shop.chobitok.modnyi.novaposta.request.GetTrackingRequest;
import shop.chobitok.modnyi.novaposta.request.MethodProperties;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;

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
    private OrderRepository orderRepository;
    private NPOrderMapper npOrderMapper;

    public StatisticService(NovaPostaRepository postaRepository, NovaPostaService novaPostaService, OrderRepository orderRepository, NPOrderMapper npOrderMapper) {
        this.postaRepository = postaRepository;
        this.novaPostaService = novaPostaService;
        this.orderRepository = orderRepository;
        this.npOrderMapper = npOrderMapper;
    }

    public String needToBePayed(String pathToAllTTNFile, String payedTTNFile) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathToAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        List<String> payedTTNList = ShoeUtil.readTXTFile(payedTTNFile);
        Set<String> payedTTNSet = new HashSet();
        for (String s : payedTTNList) {
            payedTTNSet.add(s.replaceAll("\\s+", ""));
        }

        List<Ordered> orderedList = new ArrayList<>();
        Double sum = 0d;
        for (String s : allTTNSet) {
            if (!payedTTNSet.contains(s)) {
                TrackingEntity trackingEntity = postaRepository.getTracking(postaRepository.formGetTrackingRequest(s));
                Data data = trackingEntity.getData().get(0);
                if (ShoeUtil.convertToStatus(data.getStatusCode()) == Status.RECEIVED) {
                    stringBuilder.append(data.getNumber());
                    stringBuilder.append("\n");
                    Ordered ordered = npOrderMapper.toOrdered(trackingEntity);
                    if (ordered.getOrderedShoes().size() > 0) {
                        Shoe shoe = ordered.getOrderedShoes().get(0);
                        sum += shoe.getCost();
                    }
                    orderedList.add(ordered);
                }
            }
        }
        stringBuilder.append("\n\n");
        stringBuilder.append("Сума = " + sum);
        return stringBuilder.toString();
    }


    public String countAllReceivedAndDenied(String pathAllTTNFile) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        int received = 0;
        int denied = 0;
        for (String s : allTTNList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(postaRepository.formGetTrackingRequest(s));
            Status status = ShoeUtil.convertToStatus(trackingEntity.getData().get(0).getStatusCode());
            if (status == Status.RECEIVED) {
                ++received;
            } else if (status == Status.DENIED) {
                ++denied;
            }
        }
        stringBuilder.append("Отримані = " + received);
        stringBuilder.append("\n");
        stringBuilder.append("Відмова = " + denied);
        return stringBuilder.toString();

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

    public Double getEarnedMoney() {
        Double income = 0d;
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getStatus() == Status.RECEIVED) {
                income += ordered.getOrderedShoes().get(0).getCost();
            }
        }
        return income;
    }

    public List<String> formListForDeliveryFromFile(String path) {
        List<String> strings = ShoeUtil.readTXTFile(path);
        for (String ttn : strings) {
            TrackingEntity trackingEntity = postaRepository.getTracking(postaRepository.formGetTrackingRequest(ttn));
            Data data = trackingEntity.getData().get(0);
            if (ShoeUtil.convertToStatus(data.getStatusCode()) == Status.CREATED) {
                System.out.println(data.getNumber());
                System.out.println(data.getCargoDescriptionString());
                System.out.println("");
            }
        }
        return null;
    }


}
