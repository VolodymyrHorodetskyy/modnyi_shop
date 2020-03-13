package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StatisticService {

    private NovaPostaRepository postaRepository;
    private NovaPostaService novaPostaService;
    private OrderRepository orderRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;


    public StatisticService(NovaPostaRepository postaRepository, NovaPostaService novaPostaService, OrderRepository orderRepository, NPOrderMapper npOrderMapper, NPHelper npHelper) {
        this.postaRepository = postaRepository;
        this.novaPostaService = novaPostaService;
        this.orderRepository = orderRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
    }

    public String forDelivery(String pathToFile) {
        return countDelivery(readFileToTTNSet(pathToFile));
    }

    public String forDelivery(MultipartFile file) {
        return countDelivery(readFileToTTNSet(file));
    }

    private String countDelivery(Set<String> ttnSet) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (String s : ttnSet) {
            Data data = postaRepository.getTracking(npHelper.formGetTrackingRequest(s)).getData().get(0);
            if (ShoeUtil.convertToStatus(data.getStatusCode()) == Status.CREATED) {
                ++count;
                stringBuilder.append(data.getNumber() + "\n" + data.getCargoDescriptionString());
                stringBuilder.append("\n\n");
            }
        }
        stringBuilder.append("Кількість:" + count);
        return stringBuilder.toString();
    }


    private Set<String> readFileToTTNSet(String path) {
        List<String> allTTNList = ShoeUtil.readTXTFile(path);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        return allTTNSet;
    }

    private Set<String> readFileToTTNSet(MultipartFile file) {
        List<String> allTTNList = ShoeUtil.readTXTFile(file);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        return allTTNSet;
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
                TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
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
            TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
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

    public String getAllDenied(String pathAllTTNFile, boolean returned) {
        StringBuilder stringBuilderWithDesc = new StringBuilder();
        StringBuilder stringBuilderWithoutDesc = new StringBuilder();
        List<String> allTTNList = ShoeUtil.readTXTFile(pathAllTTNFile);
        Set<String> allTTNSet = new HashSet();
        for (String s : allTTNList) {
            allTTNSet.add(s.replaceAll("\\s+", ""));
        }
        stringBuilderWithDesc.append("З описом \n");
        stringBuilderWithoutDesc.append("ТТН \n");
        for (String s : allTTNList) {
            TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(s));
            Data data = trackingEntity.getData().get(0);
            if (data.getStatusCode().equals(103)) {
                stringBuilderWithDesc.append(data.getNumber() + " " + data.getCargoDescriptionString() + "\n");
                stringBuilderWithoutDesc.append(data.getNumber() + "\n");
            }
        }
        stringBuilderWithDesc.append(stringBuilderWithoutDesc.toString());
        return stringBuilderWithDesc.toString();
    }


}
