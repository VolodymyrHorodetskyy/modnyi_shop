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
import java.util.List;
import java.util.stream.Collectors;


@Service
public class NPOrderMapper {

    private ShoeService shoeService;
    private ClientRepository clientRepository;

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
                ordered.setStatus(ShoeUtil.convertToStatus(Integer.parseInt(data.getStatusCode())));
                ordered.setPostComment(data.getCargoDescriptionString());
                ordered.setLastTransactionDateTime(ShoeUtil.toLocalDateTime(data.getLastTransactionDateTimeGM()));
                ordered.setClient(parseClient(data));
                ordered.setReturnSumNP(data.getRedeliverySum());
                ordered.setNameAndSurnameNP(data.getRecipientFullNameEW());
                ordered.setLastCreatedOnTheBasisDocumentTypeNP(data.getLastCreatedOnTheBasisDocumentType());
                ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));

                setShoeAndSizeFromDescriptionNP(ordered, data.getCargoDescriptionString());
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
        List<Shoe> orderedShoes = null;
        try {
            String model = string.substring(0, string.indexOf(' '));
            String color = string.substring(string.indexOf(' ') + 1);
            color = color.substring(0, color.indexOf(","));
            List<Shoe> shoes = shoeService.getAll(0, 20, model);
            List<Shoe> byModel = shoes.stream().filter(shoe -> shoe.getModel().contains(model)).collect(Collectors.toList());
            if (byModel.size() > 0) {
                orderedShoes = new ArrayList<>();
                String finalColor = color;
                List<Shoe> byColor = byModel.stream().filter(shoe -> shoe.getColor().contains(finalColor)).collect(Collectors.toList());
                if (byColor.size() > 0) {
                    orderedShoes.add(byColor.get(0));
                } else {
                    orderedShoes.add(byModel.get(0));
                }
            }
            ordered.setOrderedShoes(orderedShoes);
        } catch (StringIndexOutOfBoundsException e) {
     //       e.printStackTrace();
        }
        try {
            String size = string.substring(string.indexOf(",") + 1);
            size = size.trim();
            size = size.substring(0, size.indexOf(' '));
            ordered.setSize(Integer.parseInt(size));
            ordered.setSize(Integer.parseInt(size));
        } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
       //     e.printStackTrace();
        }
    }

}
