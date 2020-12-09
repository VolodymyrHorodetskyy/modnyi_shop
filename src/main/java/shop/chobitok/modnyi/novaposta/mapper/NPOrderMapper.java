package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.service.ClientService;
import shop.chobitok.modnyi.service.PropsService;
import shop.chobitok.modnyi.service.ShoePriceService;
import shop.chobitok.modnyi.service.ShoeService;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class NPOrderMapper {

    private ShoeRepository shoeRepository;
    private ClientService clientService;
    private ShoePriceService shoePriceService;

    private List<Integer> sizes = Arrays.asList(36, 37, 38, 39, 40, 41);

    private PropsService propsService;

    public NPOrderMapper(ShoeRepository shoeRepository, ClientService clientService, ShoePriceService shoePriceService, PropsService propsService) {
        this.shoeRepository = shoeRepository;
        this.clientService = clientService;
        this.shoePriceService = shoePriceService;
        this.propsService = propsService;
    }

    public Ordered toOrdered(Ordered ordered, TrackingEntity trackingEntity, Discount discount) {
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
                ordered.setDiscount(discount);
                if (ordered.getClient() == null) {
                    ordered.setClient(clientService.parseClient(data));
                }
                ordered.setReturnSumNP(data.getRedeliverySum());
                ordered.setNameAndSurnameNP(data.getRecipientFullNameEW());
                ordered.setLastCreatedOnTheBasisDocumentTypeNP(data.getLastCreatedOnTheBasisDocumentType());
                ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(data.getDateCreated()));
                if (ordered.getOrderedShoes() == null || ordered.getOrderedShoes().size() == 0) {
                    setShoeAndSizeFromDescriptionNP(ordered, data.getCargoDescriptionString());
                }
                if (ordered.getPrice() == null || ordered.getPrice() == 0d) {
                    setPriceAndPrepayment(ordered, data, discount);
                }
                if (ordered.getNpAccountId() == null) {
                    ordered.setNpAccountId(propsService.getActual().getId());
                }
            }
        }
        return ordered;
    }

    public Ordered toOrdered(TrackingEntity trackingEntity, Discount discount) {
        return toOrdered(null, trackingEntity, discount);
    }

    public Ordered toOrdered(ListTrackingEntity entity, String ttn, Discount discount) {
        List<DataForList> list = entity.getData();
        Ordered ordered = null;
        if (list.size() > 0) {
            DataForList filteredData = list.stream().filter(dataForList -> dataForList.getIntDocNumber().equals(ttn)).findFirst().orElse(null);
            if (filteredData != null) {
                ordered = new Ordered();
                ordered.setTtn(filteredData.getIntDocNumber());
                ordered.setClient(clientService.parseClient(filteredData.getRecipientContactPerson(), filteredData.getRecipientsPhone()));
                ordered.setAddress(filteredData.getRecipientAddressDescription());
                ordered.setStatus(Status.СТВОРЕНО);
                ordered.setStatusNP(1);
                ordered.setPostComment(filteredData.getDescription());
                ordered.setReturnSumNP(Double.valueOf(filteredData.getBackwardDeliveryMoney()));
                ordered.setNameAndSurnameNP(filteredData.getRecipientContactPerson());
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(filteredData.getDateTime()));
                ordered.setDiscount(discount);
                //TODO: setLastCreatedOnTheBasisDocumentTypeNP ?
                if (ordered.getOrderedShoes() == null || ordered.getOrderedShoes().size() == 0) {
                    setShoeAndSizeFromDescriptionNP(ordered, filteredData.getDescription());
                }
                if (ordered.getPrice() == null || ordered.getPrice() == 0d) {
                    setPriceAndPrepayment(ordered, ordered.getReturnSumNP(), discount);
                }
                if (ordered.getNpAccountId() == null) {
                    ordered.setNpAccountId(propsService.getActual().getId());
                }
            }
        }
        return ordered;
    }


    public Shoe parseShoe(String string) {
        List<Shoe> shoes = shoeRepository.findAll(new ShoeSpecification(""), PageRequest.of(0, 300, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent();
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


    private void setPriceAndPrepayment(Ordered ordered, Data data, Discount discount) {
        setPriceAndPrepayment(ordered, data.getRedeliverySum(), discount);
    }

    public void setPriceAndPrepayment(Ordered ordered, Double redeliverySum, Discount discount) {
        Double price = 0d;
        if (ordered.getOrderedShoes() != null && ordered.getOrderedShoes().size() > 0) {
            price = countDiscount(ordered.getOrderedShoes(), discount);
            ordered.setPrice(price);
        }
        if (redeliverySum != null && redeliverySum < 2d) {
            ordered.setFullPayment(true);
            ordered.setPrePayment(price);
        } else {
            ordered.setPrePayment(100d);
        }
    }

    public Double countDiscount(List<Shoe> orderedShoes, Discount discount) {
        Double generalAmount = 0d;
        if (discount != null) {
            ShoePrice cheapestPrice = shoePriceService.getActualShoePrice(orderedShoes.get(0));
            if (discount.getShoeNumber() <= orderedShoes.size() && discount.getShoeNumber() > 1) {
                for (Shoe shoe : orderedShoes) {
                    ShoePrice shoePrice = shoePriceService.getActualShoePrice(shoe);
                    if (cheapestPrice.getPrice() > shoePrice.getPrice()) {
                        cheapestPrice = shoePrice;
                    }
                    generalAmount += shoePrice.getPrice();
                }
                generalAmount = generalAmount - cheapestPrice.getPrice();
                Double discountPrice = countDiscPercentage(cheapestPrice.getPrice(), discount.getDiscountPercentage());
                return generalAmount + discountPrice;
            } else if (discount.getShoeNumber() == 1) {
                for (Shoe shoe : orderedShoes) {
                    generalAmount += countDiscPercentage(shoePriceService.getActualShoePrice(shoe).getPrice(), discount.getDiscountPercentage());
                }
                return roundDouble(generalAmount);
            }
        }
        for (Shoe shoe : orderedShoes) {
            generalAmount += shoePriceService.getActualShoePrice(shoe).getPrice();
        }
        return generalAmount;
    }


    public Double countDiscPercentage(Double price, Integer discountPercentage) {
        return roundDouble(price - ((price / 100) * discountPercentage));
    }

    private Double roundDouble(Double toRound) {
        return Math.round(toRound * 100.0) / 100.0;
    }

}
