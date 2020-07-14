package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.service.ShoeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class NPOrderMapper {

    private ShoeService shoeService;
    private List<Integer> sizes = Arrays.asList(36, 37, 38, 39, 40);

    public NPOrderMapper(ShoeService shoeService) {
        this.shoeService = shoeService;
    }

    public Ordered toOrdered(Ordered ordered, TrackingEntity trackingEntity) {
        if (trackingEntity != null) {
            List<Data> dataList = trackingEntity.getData();
            if (dataList != null && dataList.size() > 0) {
                if (ordered == null) {
                    ordered = new Ordered();
                }
                Data data = dataList.get(0);
                ordered.setTtn(data.getNumber());
                ordered.setAddress(data.getRecipientAddress());
                ordered.setStatus(ShoeUtil.convertToStatus(data.getStatusCode()));
                ordered.setStatusNP(data.getStatusCode());
                ordered.setPostComment(data.getCargoDescriptionString());
                ordered.setLastTransactionDateTime(ShoeUtil.toLocalDateTime(data.getLastTransactionDateTimeGM()));
                ordered.setClient(parseClient(data));
                ordered.setReturnSumNP(data.getRedeliverySum());
                ordered.setNameAndSurnameNP(data.getRecipientFullNameEW());
                ordered.setLastCreatedOnTheBasisDocumentTypeNP(data.getLastCreatedOnTheBasisDocumentType());
                ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(data.getDateCreated()));
                setShoeAndSizeFromDescriptionNP(ordered, data.getCargoDescriptionString());
                setPriceAndPrepayment(ordered, data);
            }
        }
        return ordered;
    }

    public Ordered toOrdered(TrackingEntity trackingEntity) {
        return toOrdered(null, trackingEntity);
    }

    public Ordered toOrdered(ListTrackingEntity entity, String ttn) {
        List<DataForList> list = entity.getData();
        Ordered ordered = null;
        if (list.size() > 0) {
            DataForList filteredData = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ttn)).findFirst().orElse(null);
            if (filteredData != null) {
                ordered = new Ordered();
                ordered.setTtn(filteredData.getIntDocNumber());
                ordered.setClient(parseClient(filteredData.getRecipientContactPerson(), filteredData.getRecipientsPhone()));
                ordered.setAddress(filteredData.getRecipientAddressDescription());
                ordered.setStatus(Status.СТВОРЕНО);
                ordered.setStatusNP(1);
                ordered.setPostComment(filteredData.getDescription());
                ordered.setReturnSumNP(filteredData.getCost());
                ordered.setNameAndSurnameNP(filteredData.getRecipientContactPerson());
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(filteredData.getDateTime()));
                //TODO: setLastCreatedOnTheBasisDocumentTypeNP ?
                setShoeAndSizeFromDescriptionNP(ordered, filteredData.getDescription());
                setPriceAndPrepayment(ordered, filteredData.getCost());
            }
        }
        return ordered;
    }


    private Client parseClient(Data data) {
        return parseClient(data.getRecipientFullName(), data.getPhoneRecipient());
    }

    private Client parseClient(String clientFullName, String phoneRecipient) {
        Client client = null;
        if (!StringUtils.isEmpty(clientFullName)) {
            client = new Client();
            String[] strings = clientFullName.split(" ");
            if (strings.length > 0) {
                client.setLastName(strings[0]);
                client.setName(strings[1]);
                if (strings.length > 2) {
                    client.setMiddleName(strings[2]);
                }
            }
            client.setPhone("+" + phoneRecipient);
        }
        return client;
    }

    public Shoe parseShoe(String string) {
        List<Shoe> shoes = shoeService.getAll(0, 100, "");
        List<Shoe> matched = new ArrayList<>();
        for (Shoe shoe : shoes) {
            if (StringUtils.isEmpty(shoe.getPatterns())) {
                continue;
            }
            if (matched.size() > 1) {
                return null;
            }

            for (String pattern : shoe.getPatterns()) {
                if (shoe.getPatterns() == null) {
                    return null;
                }
                pattern = pattern.replace("\\\\", "\\");
                if (string.toLowerCase().matches(pattern)) {
                    matched.add(shoe);
                    break;
                }

            }
        }
        if (matched.size() == 1) {
            return matched.get(0);
        } else {
            return null;
        }
    }

    private void setShoeAndSizeFromDescriptionNP(Ordered ordered, String string) {
        Integer size = null;
        for (Integer size1 : sizes) {
            if (string.contains(size1.toString())) {
                size = size1;
            }
        }
        List<Shoe> shoeList = new ArrayList<>();
        Shoe shoe = parseShoe(string);
        if (shoe != null) {
            shoeList.add(shoe);
        }
        if (size != null) {
            ordered.setSize(size);
        }
        ordered.setOrderedShoes(shoeList);
    }


    private void setPriceAndPrepayment(Ordered ordered, Data data) {
        setPriceAndPrepayment(ordered, data.getRedeliverySum());
    }

    private void setPriceAndPrepayment(Ordered ordered, Double redeliverySum) {
        if (ordered.getOrderedShoes() != null && ordered.getOrderedShoes().size() > 0) {
            Shoe shoe = ordered.getOrderedShoes().get(0);
            if (shoe != null) {
                Double prepayment = shoe.getPrice() - redeliverySum;
                ordered.setPrice(shoe.getPrice());
                if (prepayment < 0) {
                    prepayment = 0d;
                } else if (prepayment == 99) {
                    prepayment = 100d;
                }
                ordered.setPrePayment(prepayment);
            }
        }
    }


}
