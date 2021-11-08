package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.request.CancelOrderWithIdRequest;
import shop.chobitok.modnyi.entity.request.CancelOrderWithOrderRequest;
import shop.chobitok.modnyi.entity.response.GetCanceledResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.DateHelper.formDateTime;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;

@Service
public class CanceledOrderReasonService {

    private OrderRepository orderRepository;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    private NovaPostaRepository postaRepository;
    private MailService mailService;
    private StatusChangeService statusChangeService;
    private ShoePriceService shoePriceService;
    private GoogleDocsService googleDocsService;
    private PayedOrderedService payedOrderedService;
    private ImportService importService;

    public CanceledOrderReasonService(OrderRepository orderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, NovaPostaRepository postaRepository, MailService mailService, StatusChangeService statusChangeService, ShoePriceService shoePriceService, GoogleDocsService googleDocsService, PayedOrderedService payedOrderedService, ImportService importService) {
        this.orderRepository = orderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.postaRepository = postaRepository;
        this.mailService = mailService;
        this.statusChangeService = statusChangeService;
        this.shoePriceService = shoePriceService;
        this.googleDocsService = googleDocsService;
        this.payedOrderedService = payedOrderedService;
        this.importService = importService;
    }

    public CanceledOrderReason getCanceledOrderReasonByOrderedId(Long id) {
        return canceledOrderReasonRepository.findFirstByOrderedId(id);
    }

    public Ordered cancelOrder(CancelOrderWithOrderRequest cancelOrderRequest) {
        Ordered ordered = orderRepository.findById(cancelOrderRequest.getOrderId()).orElse(null);
        if (ordered == null) {
            throw new ConflictException("Немає такого замовлення");
        }
        String newTTN = cancelOrderRequest.getNewTTN();
        importNewTtnFromCanceled(newTTN, ordered.getUser().getId());
        statusChangeService.createRecord(ordered, ordered.getStatus(), Status.ВІДМОВА);
        payedOrderedService.createPayedOrdered(ordered);
        ordered.setStatus(Status.ВІДМОВА);
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(cancelOrderRequest.getOrderId());
        if (canceledOrderReason == null) {
            canceledOrderReason = new CanceledOrderReason(ordered, cancelOrderRequest.getReason(), cancelOrderRequest.getComment(),
                    newTTN, cancelOrderRequest.getReturnTTN());
        } else {
            canceledOrderReason.setComment(cancelOrderRequest.getComment());
            canceledOrderReason.setReason(cancelOrderRequest.getReason());
            canceledOrderReason.setNewTtn(newTTN);
            canceledOrderReason.setReturnTtn(cancelOrderRequest.getReturnTTN());
        }
        canceledOrderReason.setManual(true);
        orderRepository.save(ordered);
        canceledOrderReasonRepository.save(canceledOrderReason);
        return ordered;
    }

    private String importNewTtnFromCanceled(String newTtn, Long userId) {
        String result = null;
        if (newTtn != null && !newTtn.isBlank()) {
            if (orderRepository.findOneByAvailableTrueAndTtn(newTtn) == null) {
                result = importService.importOrderFromTTNString(newTtn, userId, null);
            }
        }
        return result;
    }

    private void sendMailIfPayed(Ordered ordered) {
        if (ordered.getStatus() != Status.ВІДМОВА && ordered.isPayed()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ordered.getTtn());
            Double generalSum = 0d;
            for (OrderedShoe orderedShoe : ordered.getOrderedShoeList()) {
                generalSum += shoePriceService.getShoePrice(orderedShoe.getShoe(), ordered).getCost();
            }
            stringBuilder.append("\n").append("Сума : ").append(generalSum);
            mailService.sendEmail("Було оплачено", stringBuilder.toString(), "horodetskyyv@gmail.com");
        }
    }

    public CanceledOrderReason createDefaultReasonOnCancel(Ordered ordered) {
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId());
        if (canceledOrderReason != null) {
            return null;
        }
        canceledOrderReason = new CanceledOrderReason();
        canceledOrderReason.setOrdered(ordered);
        canceledOrderReason.setReason(CancelReason.НЕ_ВИЗНАЧЕНО);
        return canceledOrderReasonRepository.save(canceledOrderReason);
    }

    public List<CanceledOrderReason> checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime from) {
        List<Ordered> orderedList = orderRepository.findAll(new OrderedSpecification(from, null, Status.ВІДМОВА));
        List<CanceledOrderReason> canceledOrderReasons = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId()) == null) {
                canceledOrderReasons.add(createDefaultReasonOnCancel(ordered));
            }
        }

        return canceledOrderReasons;
    }

    @Transactional
    public List<CanceledOrderReason> setReturnTtnAndUpdateStatus() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusMonths(1), true));
        List<CanceledOrderReason> updated = new ArrayList<>();
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (isEmpty(canceledOrderReason.getReturnTtn()) && canceledOrderReason.getOrdered() != null
                    && !isEmpty(canceledOrderReason.getOrdered().getTtn()) && !canceledOrderReason.isManual()) {
                Data returned = getReturnedEntity(canceledOrderReason.getOrdered().getTtn());
                if (returned != null) {
                    canceledOrderReason.setReturnTtn(returned.getNumber());
                    canceledOrderReason.setStatus(convertToStatus(returned.getStatusCode()));
                    canceledOrderReason.setDeliveryCost(Double.valueOf(returned.getDocumentCost()));
                    canceledOrderReason.setStoragePrice(!isEmpty(returned.getStoragePrice().isEmpty()) ? Double.valueOf(returned.getStoragePrice()) : null);
                    if (returned.getDatePayedKeeping() != null && canceledOrderReason.getDatePayedKeeping() == null) {
                        canceledOrderReason.setDatePayedKeeping(ShoeUtil.toLocalDateTime(returned.getDatePayedKeeping()));
                    }
                    updated.add(canceledOrderReason);
                }
            } else if (!isEmpty(canceledOrderReason.getReturnTtn())) {
                Data returned = postaRepository.getTracking(null, canceledOrderReason.getReturnTtn()).getData().get(0);
                canceledOrderReason.setStatus(convertToStatus(returned.getStatusCode()));
                canceledOrderReason.setDeliveryCost(Double.valueOf(returned.getDocumentCost()));
                canceledOrderReason.setStoragePrice(!isEmpty(returned.getStoragePrice()) ? Double.valueOf(returned.getStoragePrice()) : null);
                if (returned.getDatePayedKeeping() != null && canceledOrderReason.getDatePayedKeeping() == null) {
                    canceledOrderReason.setDatePayedKeeping(ShoeUtil.toLocalDateTime(returned.getDatePayedKeeping()));
                }
                updated.add(canceledOrderReason);
            }
        }
        List<CanceledOrderReason> done = canceledOrderReasonRepository.saveAll(updated);
        return done;
    }

    public Data getReturnedEntity(String ttn) {
        TrackingEntity trackingEntity = postaRepository.getTracking(null, ttn);
        Data returned = null;
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (!isEmpty(data.getLastCreatedOnTheBasisNumber())) {
                returned = postaRepository.getTracking(null, data.getLastCreatedOnTheBasisNumber()).getData().get(0);
                if (convertToStatus(returned.getStatusCode()) == Status.ЗМІНА_АДРЕСУ) {
                    returned = postaRepository.getTracking(null, returned.getLastCreatedOnTheBasisNumber()).getData().get(0);
                }
            }
        }
        return returned;
    }


    public GetCanceledResponse getAll(int page, int size, String ttn, String phoneOrName, Boolean manual, Boolean withoutReason, String userId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page page1 = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusMonths(1), false, removeSpaces(ttn), phoneOrName, manual, withoutReason, userId), pageRequest);
        return new GetCanceledResponse(page1.getContent(), page1.getTotalElements());
    }

    public CanceledOrderReason setReason(CancelOrderWithIdRequest cancelOrderWithIdRequest) {
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findById(cancelOrderWithIdRequest.getId()).orElse(null);
        if (canceledOrderReason == null) {
            throw new ConflictException("CancelOrderReason не існує");
        }
        String newTTN = cancelOrderWithIdRequest.getNewTTN();
        importNewTtnFromCanceled(newTTN, canceledOrderReason.getOrdered().getUser().getId());
        canceledOrderReason.setNewTtn(newTTN);
        canceledOrderReason.setReason(cancelOrderWithIdRequest.getReason());
        canceledOrderReason.setComment(cancelOrderWithIdRequest.getComment());
        canceledOrderReason.setReturnTtn(cancelOrderWithIdRequest.getReturnTTN());
        return canceledOrderReasonRepository.save(canceledOrderReason);
    }

    public CanceledOrderReason getCanceledOrderReasonByOrderId(Long orderedId) {
        return canceledOrderReasonRepository.findFirstByOrderedId(orderedId);
    }

    public CanceledOrderReason getById(Long id) {
        return canceledOrderReasonRepository.findById(id).orElse(null);
    }

    public StringResponse getReturned(boolean excludeFromDeliveryFile, boolean showOnlyImportant,
                                      boolean showClientTtn, boolean showOnlyDelivered, String dateFrom) {
        List<Ordered> toSave = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<CanceledOrderReason> canceledOrderReasons;
        CanceledOrderReasonSpecification canceledOrderReasonSpecification = new CanceledOrderReasonSpecification();
        canceledOrderReasonSpecification.setStatusNotReceived(true);
        canceledOrderReasonSpecification.setHasReturnTtn(true);
        if (showOnlyDelivered) {
            canceledOrderReasonSpecification.setStatus(Status.ДОСТАВЛЕНО);
        }
        if (!isEmpty(dateFrom)) {
            canceledOrderReasonSpecification.setLastModifiedDate(formDateTime(dateFrom));
        }
        canceledOrderReasons = canceledOrderReasonRepository.findAll(canceledOrderReasonSpecification);

       /*
       for find coincidences
       List<Ordered> orderedList = orderRepository.findByNotForDeliveryFileTrue();
        for (Ordered order : orderedList) {
            order.setNotForDeliveryFile(false);
            toSave.add(order);
        }*/
        //  List<Ordered> createdList = orderRepository.findAllByAvailableTrueAndStatusInOrderByUrgentDesc(Arrays.asList(Status.СТВОРЕНО));
        Set<CanceledOrderReason> used = new HashSet<>();
        Set<CanceledOrderReason> toFind = new HashSet<>();
        int countArrived = 0;
        Double sumToPayForDelivery = 0d;
        Double sumToPayPaidKeeping = 0d;
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            sumToPayForDelivery += countDeliveryCost(canceledOrderReason);
            sumToPayPaidKeeping += countPayedKeepingCost(canceledOrderReason);
            if (canceledOrderReason.getReason() == CancelReason.БРАК || canceledOrderReason.getReason() == CancelReason.ПОМИЛКА) {
                used.add(canceledOrderReason);
            } else {
                toFind.add(canceledOrderReason);
                if (!showOnlyImportant && !used.contains(canceledOrderReason)
                        && showOnlyDelivered ? canceledOrderReason.getStatus() == Status.ДОСТАВЛЕНО : true) {
                    appendReturnTtns(result, showClientTtn, canceledOrderReason);
                }
            }
            if (canceledOrderReason.getStatus() == Status.ДОСТАВЛЕНО) {
                ++countArrived;
            }
        }
        //  String coincidencesString = getCoincidences(createdList, toFind, used, excludeFromDeliveryFile, toSave);
        result.append(getPayedKeeping(canceledOrderReasons.stream().filter(canceledOrderReason -> canceledOrderReason.getDatePayedKeeping() != null).collect(Collectors.toList())));
        result.append(getPayAttention(showOnlyImportant, showClientTtn, used));
        //    result.append(coincidencesString);

        result.append("Кількість доставлених: ").append(countArrived).append("\n\n");

        if (toSave.size() > 0) {
            orderRepository.saveAll(toSave);
        }
        result.append("Сума доставки: ").append(sumToPayForDelivery).append("\n");
        result.append("Платні зберігання: ").append(sumToPayPaidKeeping).append("\n");
        result.append("Загальна сума: ").append(sumToPayForDelivery + sumToPayPaidKeeping);
        String resultString = result.toString();
        googleDocsService.updateReturningsFile(resultString);
        return new StringResponse(resultString);
    }

    private Double countDeliveryCost(CanceledOrderReason canceledOrderReason) {
        Double sumToPayForDelivery = 0d;
        sumToPayForDelivery += canceledOrderReason.getDeliveryCost() != null ?
                canceledOrderReason.getDeliveryCost() : 0;
        sumToPayForDelivery += canceledOrderReason.getOrdered().getDeliveryCost() != null ?
                canceledOrderReason.getDeliveryCost() : 0;
        return sumToPayForDelivery;
    }

    private Double countPayedKeepingCost(CanceledOrderReason canceledOrderReason) {
        Double sumPayedKeeping = 0d;
        sumPayedKeeping += canceledOrderReason.getStoragePrice() != null ?
                canceledOrderReason.getStoragePrice() : 0;
        sumPayedKeeping += canceledOrderReason.getOrdered().getStoragePrice() != null ?
                canceledOrderReason.getOrdered().getStoragePrice() : 0;
        return sumPayedKeeping;
    }

    private void appendReturnTtns(StringBuilder stringBuilder, boolean showClientTtn,
                                  CanceledOrderReason canceledOrderReason) {
        stringBuilder.append(showClientTtn ? canceledOrderReason.getOrdered().getTtn() : "").append(showClientTtn ? "\n" : "").append(canceledOrderReason.getReturnTtn()).append(" ")
                .append(canceledOrderReason.getStatus()).append(" ").append("\n").append(canceledOrderReason.getReason())
                .append(" ").append(isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                .append("\n").append(canceledOrderReason.getOrdered().getPostComment())
                .append("\n\n");
    }

    /*
        private String getCoincidences(List<Ordered> createdList,
                                       Set<CanceledOrderReason> toFind, Set<CanceledOrderReason> used,
                                       boolean excludeFromDeliveryFile, List<Ordered> toSave) {
            StringBuilder result = new StringBuilder();
            boolean foundFirst = false;
            for (Ordered ordered : createdList) {
                for (CanceledOrderReason canceledOrderReason : toFind) {
                    if (formInside(canceledOrderReason.getOrdered().getPostComment()) == formInside(ordered.getPostComment()) &&
                            compareShoeArrays(canceledOrderReason.getOrdered().getOrderedShoes(), ordered.getOrderedShoes()) &&
                            ordered.getSize().equals(canceledOrderReason.getOrdered().getSize()) &&
                            used.add(canceledOrderReason)) {
                        if (foundFirst) {
                            result.append("Співпадіння\n\n");
                            foundFirst = true;
                        }
                        result.append(ordered.getTtn() + " " + ordered.getPostComment() + "\n");
                        result.append(canceledOrderReason.getReturnTtn() + " " + canceledOrderReason.getStatus() + " " + canceledOrderReason.getReason() + "\n\n");
                        if (excludeFromDeliveryFile) {
                            ordered.setNotForDeliveryFile(true);
                            toSave.add(ordered);
                        }
                        break;
                    }
                }
            }
            return result.toString();
        }
    */
    private String getPayedKeeping(List<CanceledOrderReason> canceledOrderReasons) {
        canceledOrderReasons.sort(Comparator.comparing(canceledOrderReason -> canceledOrderReason.getDatePayedKeeping()));
        StringBuilder result = new StringBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        boolean exist = false;
        result.append("Платне зберігання").append("\n\n");
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (canceledOrderReason.getDatePayedKeeping() != null &&
                    LocalDateTime.now().plusDays(2).isAfter(canceledOrderReason.getDatePayedKeeping())) {
                exist = true;
                result.append(canceledOrderReason.getOrdered().getPostComment()).append("\n").
                        append(canceledOrderReason.getOrdered().getTtn()).append("\n").append(canceledOrderReason.getReturnTtn()).append(" ")
                        .append(canceledOrderReason.getStatus()).append(" ").append(canceledOrderReason.getReason())
                        .append(" ").append(isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                        .append("\n")
                        .append(canceledOrderReason.getDatePayedKeeping().format(timeFormatter))
                        .append("\n\n");
            }
        }
        if (exist) {
            return result.toString();
        } else {
            return "";
        }
    }

    private String getPayAttention(boolean showOnlyImportant, boolean showClientTtn, Set<CanceledOrderReason> used) {
        StringBuilder result = new StringBuilder();
        Set<CanceledOrderReason> used2;
        if (showOnlyImportant) {
            used2 = used.stream().filter(canceledOrderReason -> canceledOrderReason.getStatus() == Status.ДОСТАВЛЕНО).collect(Collectors.toSet());
        } else {
            used2 = used;
        }
        if (used.size() > 0) {
            result.append("Звернути увагу\n\n");
        }
        for (CanceledOrderReason canceledOrderReason : used2) {
            result.append(showClientTtn ? canceledOrderReason.getOrdered().getTtn() : "").append(showClientTtn ? "\n" : "")
                    .append(canceledOrderReason.getReturnTtn()).append(" ").append(canceledOrderReason.getStatus()).append("\n")
                    .append(canceledOrderReason.getReason()).append(" ").append(isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                    .append("\n\n");

        }
        return result.toString();
    }

/*    private String getPayedKeepingOurTtn(List<OurTTN> ourTTNS) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        result.append("Наші Ттн").append("\n\n");
        boolean exist = false;
        for (OurTTN ourTTN : ourTTNS) {
            if (ourTTN.getDatePayedKeeping() != null &&
                    LocalDateTime.now().plusDays(2).isAfter(ourTTN.getDatePayedKeeping())) {
                result.append(ourTTN.getTtn()).append("\n")
                        .append(ourTTN.getDatePayedKeeping().format(timeFormatter)).append("\n\n");
            }
        }
        if (exist) {
            return result.toString();
        } else {
            return "";
        }
    }*/

    private Inside formInside(String description) {
        description = description.toLowerCase();
        if (description.contains("хут") || description.contains("мех")) {
            return Inside.fur;
        }
        return Inside.fable;
    }

    enum Inside {
        fable, fur
    }

    private boolean compareShoeArrays(List<Shoe> shoes, List<Shoe> shoes2) {
        Set<Shoe> shoesSet = new HashSet<>();
        if (shoes.size() != shoes2.size()) {
            return false;
        }
        for (Shoe shoe : shoes) {
            for (Shoe shoe1 : shoes2) {
                if (!(shoe.equals(shoe1) && shoesSet.add(shoe1))) {
                    return false;
                }
            }
        }
        return true;
    }

    public CanceledOrderReason getByReturnTtn(String ttn) {
        return canceledOrderReasonRepository.findFirstByReturnTtn(ttn);
    }


}
