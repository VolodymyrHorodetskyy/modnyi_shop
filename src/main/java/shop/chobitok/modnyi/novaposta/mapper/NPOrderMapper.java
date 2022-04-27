package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.service.CardService;
import shop.chobitok.modnyi.service.ClientService;
import shop.chobitok.modnyi.service.NpAccountService;
import shop.chobitok.modnyi.service.ShoePriceService;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;


@Service
public class NPOrderMapper {

    private ShoeRepository shoeRepository;
    private ClientService clientService;
    private ShoePriceService shoePriceService;
    private CardService cardService;

    private List<Integer> sizes = Arrays.asList(36, 37, 38, 39, 40, 41);

    private NpAccountService npAccountService;

    public NPOrderMapper(ShoeRepository shoeRepository, ClientService clientService, ShoePriceService shoePriceService, CardService cardService, NpAccountService npAccountService) {
        this.shoeRepository = shoeRepository;
        this.clientService = clientService;
        this.shoePriceService = shoePriceService;
        this.cardService = cardService;
        this.npAccountService = npAccountService;
    }

    public Ordered toOrdered(Ordered ordered, TrackingEntity trackingEntity, Discount discount,
                             Variants sourceOfOrder) {
        if (trackingEntity != null) {
            List<Data> dataList = trackingEntity.getData();
            if (dataList != null && dataList.size() > 0) {
                if (ordered == null) {
                    ordered = new Ordered();
                }
                Data data = dataList.get(0);
                ordered.setTtn(data.getNumber());
                ordered.setAddress(data.getRecipientAddress());
                ordered.setCity(data.getCityRecipient());
                ordered.setCityRefNP(data.getRefCityRecipient());
                ordered.setStatus(ShoeUtil.convertToStatus(data.getStatusCode()));
                ordered.setStatusNP(data.getStatusCode());
                ordered.setPostComment(data.getCargoDescriptionString());
                ordered.setLastTransactionDateTime(ShoeUtil.toLocalDateTime(data.getLastTransactionDateTimeGM()));
                ordered.setDiscount(discount);
                ordered.setCard(cardService.getOrSaveAndGetCardByName(data.getCardMaskedNumber()));
                if (ordered.getClient() == null) {
                    ordered.setClient(clientService.parseClient(data));
                }
                ordered.setReturnSumNP(data.getRedeliverySum());
                ordered.setNameAndSurnameNP(data.getRecipientFullNameEW());
                ordered.setLastCreatedOnTheBasisDocumentTypeNP(data.getLastCreatedOnTheBasisDocumentType());
                ordered.setDatePayedKeepingNP(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                ordered.setDateCreated(ShoeUtil.toLocalDateTime(data.getDateCreated()));
                ordered.setDeliveryCost(Double.valueOf(data.getDocumentCost()));
                ordered.setStoragePrice(!data.getStoragePrice().isEmpty() ? Double.valueOf(data.getStoragePrice()) : null);
                ordered.setSourceOfOrder(sourceOfOrder);
                if (ordered.getOrderedShoeList() == null || ordered.getOrderedShoeList().size() == 0) {
                    setShoeAndSizeFromDescriptionNP(ordered, data.getCargoDescriptionString());
                }
                if (ordered.getPrice() == null || ordered.getPrice() == 0d) {
                    setPriceAndPrepayment(ordered, data, discount);
                }
                if (ordered.getNpAccountId() == null) {
                    ordered.setNpAccountId(npAccountService.getActual().getId());
                }
            }
        }
        return ordered;
    }

    public Ordered toOrdered(TrackingEntity trackingEntity, Discount discount, Variants sourceOfOrder) {
        return toOrdered(null, trackingEntity, discount, sourceOfOrder);
    }

    public Ordered toOrdered(DataForList dataForList, Discount discount, Variants sourceOfOrder) {
        Ordered ordered = null;
        if (dataForList != null) {
            ordered = new Ordered();
            ordered.setTtn(dataForList.getIntDocNumber());
            ordered.setClient(clientService.parseClient(dataForList.getRecipientContactPerson(), dataForList.getRecipientsPhone()));
            ordered.setAddress(dataForList.getRecipientAddressDescription());
            ordered.setCity(dataForList.getCityRecipientDescription());
            ordered.setCityRefNP(dataForList.getCityRecipient());
            ordered.setStatus(Status.СТВОРЕНО);
            ordered.setStatusNP(1);
            ordered.setPostComment(dataForList.getDescription());
            ordered.setReturnSumNP(Double.valueOf(dataForList.getBackwardDeliveryMoney()));
            ordered.setNameAndSurnameNP(dataForList.getRecipientContactPerson());
            ordered.setDateCreated(ShoeUtil.toLocalDateTime(dataForList.getDateTime()));
            ordered.setCard(cardService.getOrSaveAndGetCardByName(dataForList.getRedeliveryPaymentCard()));
            ordered.setDiscount(discount);
            ordered.setDeliveryCost(dataForList.getCostOnSite() != null ?
                    Double.valueOf(dataForList.getCostOnSite()) : null);
            ordered.setSourceOfOrder(sourceOfOrder);
            //TODO: setLastCreatedOnTheBasisDocumentTypeNP ?
            if (ordered.getOrderedShoeList() == null || ordered.getOrderedShoeList().size() == 0) {
                setShoeAndSizeFromDescriptionNP(ordered, dataForList.getDescription());
            }
            if (ordered.getPrice() == null || ordered.getPrice() == 0d) {
                setPriceAndPrepayment(ordered, ordered.getReturnSumNP(), discount);
            }
            if (ordered.getNpAccountId() == null) {
                ordered.setNpAccountId(npAccountService.getActual().getId());
            }
        }
        return ordered;
    }


    public Shoe parseShoe(String string) {
        List<Shoe> shoes = shoeRepository.findAll(new ShoeSpecification(""), PageRequest.of(0, 300, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent();
        List<Shoe> matched = new ArrayList<>();
        for (Shoe shoe : shoes) {
            if (isEmpty(shoe.getPatterns())) {
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
                if (string != null && string.toLowerCase().matches(pattern)) {
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
        if (string != null) {
            for (Integer size1 : sizes) {
                if (string.contains(size1.toString())) {
                    size = size1;
                }
            }
        }
        List<OrderedShoe> orderedShoeList = new ArrayList<>();
        Shoe shoe = parseShoe(string);
        if (shoe != null && size != null) {
            orderedShoeList.add(new OrderedShoe(size, shoe));
        }
        ordered.setOrderedShoeList(orderedShoeList);
    }

    private void setPriceAndPrepayment(Ordered ordered, Data data, Discount discount) {
        setPriceAndPrepayment(ordered, data.getRedeliverySum(), discount);
    }

    public void setPriceAndPrepayment(Ordered ordered, Double redeliverySum, Discount discount) {
        Double price = 0d;
        if (ordered.getOrderedShoeList() != null && ordered.getOrderedShoeList().size() > 0) {
            price = countDiscount(ordered.getOrderedShoeList(), discount);
            ordered.setPrice(price);
        }
        if (redeliverySum != null && redeliverySum < 2d) {
            ordered.setFullPayment(true);
            ordered.setPrePayment(price);
        } else {
            ordered.setPrePayment(100d);
        }
    }

    public Double countDiscount(List<OrderedShoe> orderedShoeList, Discount discount) {
        Double generalAmount = 0d;
        if (discount != null) {
            ShoePrice cheapestPrice = shoePriceService.getActualShoePrice(orderedShoeList.get(0).getShoe());
            if (cheapestPrice == null) {
                return 0d;
            }
            if (discount.getShoeNumber() <= orderedShoeList.size() && discount.getShoeNumber() > 1) {
                for (OrderedShoe orderedShoe : orderedShoeList) {
                    ShoePrice actualShoePrice = shoePriceService.getActualShoePrice(orderedShoe.getShoe());
                    if (actualShoePrice == null) {
                        return 0d;
                    }
                    if (cheapestPrice.getPrice() > actualShoePrice.getPrice()) {
                        cheapestPrice = actualShoePrice;
                    }
                    generalAmount += actualShoePrice.getPrice();
                }
                generalAmount = generalAmount - cheapestPrice.getPrice();
                Double discountPrice = countDiscPercentage(cheapestPrice.getPrice(), discount.getDiscountPercentage());
                return generalAmount + discountPrice;
            } else if (discount.getShoeNumber() == 1) {
                for (OrderedShoe orderedShoe : orderedShoeList) {
                    ShoePrice actualShoePrice = shoePriceService.getActualShoePrice(orderedShoe.getShoe());
                    if (actualShoePrice == null) {
                        return 0d;
                    }
                    generalAmount += countDiscPercentage(actualShoePrice.getPrice(), discount.getDiscountPercentage());
                }
                generalAmount = roundDouble(generalAmount);
            } else if (discount.getShoeNumber() == 0) {
                generalAmount = Double.valueOf(orderedShoeList.size()) * Double.valueOf(discount.getDiscountPercentage());

            }
        } else {
            for (OrderedShoe orderedShoe : orderedShoeList) {
                ShoePrice actualShoePrice = shoePriceService.getActualShoePrice(orderedShoe.getShoe());
                if (actualShoePrice == null) {
                    return 0d;
                }
                generalAmount += actualShoePrice.getPrice();
            }
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
