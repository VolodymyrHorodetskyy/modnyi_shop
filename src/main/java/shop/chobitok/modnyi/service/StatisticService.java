package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.OrderRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private NovaPostaRepository postaRepository;
    private OrderRepository orderRepository;
    private NPOrderMapper npOrderMapper;
    private NPHelper npHelper;
    private OrderService orderService;

    public StatisticService(NovaPostaRepository postaRepository, OrderRepository orderRepository, NPOrderMapper npOrderMapper, NPHelper npHelper, OrderService orderService) {
        this.postaRepository = postaRepository;
        this.orderRepository = orderRepository;
        this.npOrderMapper = npOrderMapper;
        this.npHelper = npHelper;
        this.orderService = orderService;
    }


    //    public String countNeedDelivery(String pathToFile) {
//        return countNeedDelivery(readFileToTTNSet(pathToFile));
//    }
//
//    public String countNeedDelivery(MultipartFile file) {
//        return countNeedDelivery(readFileToTTNSet(file));
//    }

    public StringResponse countNeedDeliveryFromDB(boolean updateStatuses) {
        if (updateStatuses) {
            orderService.updateOrderStatuses();
        }
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusOrderByDateCreated(Status.CREATED);
        List<String> ttns = orderedList.stream().map(ordered -> ordered.getTtn()).collect(Collectors.toList());
        return countNeedDelivery(toTTNSet(ttns));
    }

    private StringResponse countNeedDelivery(Set<String> ttnSet) {
        List<String> stringList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (String s : ttnSet) {
            Data data = postaRepository.getTracking(npHelper.formGetTrackingRequest(s)).getData().get(0);
            if (ShoeUtil.convertToStatus(data.getStatusCode()) == Status.CREATED) {
                ++count;
                stringList.add(data.getNumber() + "\n" + data.getCargoDescriptionString());
                result.append(data.getNumber() + "\n" + data.getCargoDescriptionString() + "\n\n");
            }
        }
        result.append("Кількість :" + count);
        return new StringResponse(result.toString());
    }


    public StringResponse getIssueOrders() {
        //TODO: optimisation
        StringBuilder result = new StringBuilder();
        List<Ordered> orderedList = orderRepository.findAll();
        for (Ordered ordered : orderedList) {
            if (ordered.getOrderedShoes() == null || ordered.getOrderedShoes().size() < 1 || ordered.getSize() == null) {
                result.append(ordered.getTtn() + "  ... замовлення без взуття або розміру \n");
            }
        }
        if (result.length() == 0) {
            result.append("Помилок немає");
        }
        return new StringResponse(result.toString());
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
        return toTTNSet(ShoeUtil.readTXTFile(file));
    }

    private Set<String> toTTNSet(List<String> allTTNList) {
        Set<String> allTTNSet = new LinkedHashSet<>();
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
