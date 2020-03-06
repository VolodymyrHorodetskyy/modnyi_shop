package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.ClientRepository;
import shop.chobitok.modnyi.service.ShoeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class NPOrderMapper {

    private ShoeService shoeService;
    private ClientRepository clientRepository;
    private List<Integer> sizes = Arrays.asList(36, 37, 38, 39, 40);
    private String defaultColor = "шкіра";

    public NPOrderMapper(ShoeService shoeService, ClientRepository clientRepository) {
        this.shoeService = shoeService;
        this.clientRepository = clientRepository;
    }

    public Ordered toOrdered(TrackingEntity trackingEntity) {
        Ordered ordered = null;
        if (trackingEntity != null) {
            List<Data> dataList = trackingEntity.getData();
            if (dataList != null && dataList.size() > 0) {
                ordered = new Ordered();
                Data data = dataList.get(0);
                ordered.setTtn(data.getNumber());
                ordered.setAddress(data.getRecipientAddress());
                ordered.setStatus(ShoeUtil.convertToStatus(data.getStatusCode()));
                ordered.setPostComment(data.getCargoDescriptionString());
                ordered.setLastTransactionDateTime(ShoeUtil.toLocalDateTime(data.getLastTransactionDateTimeGM()));
                ordered.setClient(parseClient(data));
                ordered.setReturnSumNP(data.getRedeliverySum());
                ordered.setNameAndSurnameNP(data.getRecipientFullNameEW());
                ordered.setLastCreatedOnTheBasisDocumentTypeNP(data.getLastCreatedOnTheBasisDocumentType());
                ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                setShoeAndSizeFromDescriptionNP(ordered, data.getCargoDescriptionString());
                setPriceAndPrepayment(ordered, data);
            }
        }
        return ordered;
    }


    private Client parseClient(Data data) {
        Client client = null;
        String clientFullName = data.getRecipientFullNameEW();
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
            client.setPhone("+" + data.getPhoneRecipient());
        }
        return client;
    }

    private void setShoeAndSizeFromDescriptionNP(Ordered ordered, String string) {
        List<Shoe> shoes = shoeService.getAll(0, 30, "");
        String model = null;
        Shoe parsedShoe = null;
        for (Shoe shoe : shoes) {
            if (string.toLowerCase().contains(shoe.getModel().toLowerCase())) {
                model = shoe.getModel();
            }
        }
        if (!StringUtils.isEmpty(model)) {
            String finalModel = model;
            shoes = shoes.stream().filter(shoe -> shoe.getModel().contains(finalModel)).collect(Collectors.toList());
            if (shoes.size() != 1) {
                parsedShoe = shoes.stream().filter(shoe -> string.contains(shoe.getColor())).findFirst().orElse(null);
                if (parsedShoe == null) {
                    parsedShoe = shoes.stream().filter(shoe -> shoe.getColor().contains(defaultColor)).findFirst().orElse(null);
                }
            } else {
                parsedShoe = shoes.get(0);
            }
        }
        Integer size = null;
        for (Integer size1 : sizes) {
            if (string.contains(size1.toString())) {
                size = size1;
            }
        }
        List<Shoe> shoeList = new ArrayList<>();
        if (parsedShoe == null) {
            System.out.println(ordered.getTtn() + " " + string);
        } else {
            shoeList.add(parsedShoe);
        }
        if (size == null) {
            System.out.println(ordered.getTtn() + " size " + string);
        } else {
            ordered.setSize(size);
        }
        ordered.setOrderedShoes(shoeList);
    }


    private void setPriceAndPrepayment(Ordered ordered, Data data) {
        if (ordered.getOrderedShoes() != null && ordered.getOrderedShoes().size() > 0) {
            Shoe shoe = ordered.getOrderedShoes().get(0);
            if (shoe != null) {
                Double prepayment = shoe.getPrice() - data.getRedeliverySum();
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
