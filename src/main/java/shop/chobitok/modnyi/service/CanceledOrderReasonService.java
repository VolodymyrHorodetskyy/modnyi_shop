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
import shop.chobitok.modnyi.util.DateHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;

@Service
public class CanceledOrderReasonService {

    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    private NovaPostaRepository postaRepository;
    private MailService mailService;
    private StatusChangeService statusChangeService;
    private ShoePriceService shoePriceService;
    private GoogleDocsService googleDocsService;
    private PayedOrderedService payedOrderedService;
    private OrderService orderService;

    public CanceledOrderReasonService(CanceledOrderReasonRepository canceledOrderReasonRepository, NovaPostaRepository postaRepository, MailService mailService, StatusChangeService statusChangeService, ShoePriceService shoePriceService, GoogleDocsService googleDocsService, PayedOrderedService payedOrderedService, OrderService orderService) {
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.postaRepository = postaRepository;
        this.mailService = mailService;
        this.statusChangeService = statusChangeService;
        this.shoePriceService = shoePriceService;
        this.googleDocsService = googleDocsService;
        this.payedOrderedService = payedOrderedService;
        this.orderService = orderService;
    }

    public StringResponse cancelOrder(CancelOrderWithOrderRequest cancelOrderRequest) {
        Ordered ordered = orderService.findById(cancelOrderRequest.getOrderId());
        if (ordered == null) {
            throw new ConflictException("Немає такого замовлення");
        }
        statusChangeService.createRecord(ordered, ordered.getStatus(), Status.ВІДМОВА);
        payedOrderedService.createPayedOrdered(ordered);
        ordered.setStatus(Status.ВІДМОВА);
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(cancelOrderRequest.getOrderId());
        if (canceledOrderReason == null) {
            canceledOrderReason = new CanceledOrderReason(ordered, cancelOrderRequest.getReason(), cancelOrderRequest.getComment(),
                    cancelOrderRequest.getNewTTN(), cancelOrderRequest.getReturnTTN());
        } else {
            canceledOrderReason.setComment(cancelOrderRequest.getComment());
            canceledOrderReason.setReason(cancelOrderRequest.getReason());
            canceledOrderReason.setNewTtn(cancelOrderRequest.getNewTTN());
            canceledOrderReason.setReturnTtn(cancelOrderRequest.getReturnTTN());
        }
        String message = null;
        if (!StringUtils.isEmpty(cancelOrderRequest.getNewTTN())) {
            message = orderService.importOrderFromTTNString(cancelOrderRequest.getNewTTN(), ordered.getUser().getId(), ordered.getDiscount());
        }
        canceledOrderReason.setManual(true);
        orderService.saveOrder(ordered);
        canceledOrderReasonRepository.save(canceledOrderReason);
        return new StringResponse(message);
    }

    private void sendMailIfPayed(Ordered ordered) {
        if (ordered.getStatus() != Status.ВІДМОВА && ordered.isPayed()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ordered.getTtn());
            Double generalSum = 0d;
            for (Shoe shoe : ordered.getOrderedShoes()) {
                generalSum += shoePriceService.getShoePrice(shoe, ordered).getCost();
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
        List<Ordered> orderedList = orderService.getAll(from, null, Status.ВІДМОВА);
        List<CanceledOrderReason> canceledOrderReasons = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId()) == null) {
                canceledOrderReasons.add(createDefaultReasonOnCancel(ordered));
            }
        }

        return canceledOrderReasons;
    }

    public List<CanceledOrderReason> setReturnTtnAndUpdateStatus() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusMonths(1), true));
        List<CanceledOrderReason> updated = new ArrayList<>();
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (StringUtils.isEmpty(canceledOrderReason.getReturnTtn()) && canceledOrderReason.getOrdered() != null
                    && !StringUtils.isEmpty(canceledOrderReason.getOrdered().getTtn()) && !canceledOrderReason.isManual()) {
                Data returned = getReturnedEntity(canceledOrderReason.getOrdered().getTtn());
                if (returned != null) {
                    canceledOrderReason.setReturnTtn(returned.getNumber());
                    canceledOrderReason.setStatus(convertToStatus(returned.getStatusCode()));
                    if (returned.getDatePayedKeeping() != null && canceledOrderReason.getDatePayedKeeping() == null) {
                        canceledOrderReason.setDatePayedKeeping(ShoeUtil.toLocalDateTime(returned.getDatePayedKeeping()));
                    }
                    updated.add(canceledOrderReason);
                }
            } else if (!StringUtils.isEmpty(canceledOrderReason.getReturnTtn())) {
                Data returned = postaRepository.getTracking(null, canceledOrderReason.getReturnTtn()).getData().get(0);
                canceledOrderReason.setStatus(convertToStatus(returned.getStatusCode()));
                if (returned.getDatePayedKeeping() != null && canceledOrderReason.getDatePayedKeeping() == null) {
                    canceledOrderReason.setDatePayedKeeping(ShoeUtil.toLocalDateTime(returned.getDatePayedKeeping()));
                }
                updated.add(canceledOrderReason);
            }
        }
        List<CanceledOrderReason> done = canceledOrderReasonRepository.saveAll(updated);
        getReturned(true, true);
        return done;
    }

    public Data getReturnedEntity(String ttn) {
        TrackingEntity trackingEntity = postaRepository.getTracking(null, ttn);
        Data returned = null;
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (!StringUtils.isEmpty(data.getLastCreatedOnTheBasisNumber())) {
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
        canceledOrderReason.setNewTtn(cancelOrderWithIdRequest.getNewTTN());
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

    public StringResponse getReturned(boolean excludeFromDeliveryFile, boolean showOnlyImportant) {
        List<Ordered> toSave = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(true, true));
        List<Ordered> orderedList = orderService.getOrderRepository().findByNotForDeliveryFileTrue();
        for (Ordered order : orderedList) {
            order.setNotForDeliveryFile(false);
            toSave.add(order);
        }
        List<Ordered> createdList = orderService.getOrderRepository().findAllByAvailableTrueAndStatusInOrderByUrgentDesc(Arrays.asList(Status.СТВОРЕНО));
        Set<CanceledOrderReason> used = new HashSet<>();
        Set<CanceledOrderReason> toFind = new HashSet<>();
        int countArrived = 0;
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (!showOnlyImportant) {
                result.append(canceledOrderReason.getOrdered().getPostComment()).append("\n").
                        append(canceledOrderReason.getOrdered().getTtn()).append("\n").append(canceledOrderReason.getReturnTtn()).append(" ")
                        .append(canceledOrderReason.getStatus()).append(" ").append(canceledOrderReason.getReason())
                        .append(" ").append(StringUtils.isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                        .append("\n\n");
            }
            if (canceledOrderReason.getStatus() == Status.ДОСТАВЛЕНО) {
                ++countArrived;
            }
            if (canceledOrderReason.getReason() == CancelReason.БРАК) {
                used.add(canceledOrderReason);
            } else {
                toFind.add(canceledOrderReason);
            }
        }
        result.append("Кількість доставлених: ").append(countArrived).append("\n\n");

        result.append(getPayedKeeping(canceledOrderReasons.stream().filter(canceledOrderReason -> canceledOrderReason.getDatePayedKeeping() != null).collect(Collectors.toList())));

        result.append("Звернути увагу\n\n");
        Set<CanceledOrderReason> used2;
        if (showOnlyImportant) {
            used2 = used.stream().filter(canceledOrderReason -> canceledOrderReason.getStatus() == Status.ДОСТАВЛЕНО).collect(Collectors.toSet());
        } else {
            used2 = used;
        }
        for (CanceledOrderReason canceledOrderReason : used2) {
            result.append(canceledOrderReason.getOrdered().getTtn()).append("\n")
                    .append(canceledOrderReason.getReturnTtn()).append(" ").append(canceledOrderReason.getStatus()).append("\n")
                    .append(canceledOrderReason.getReason()).append(" ").append(StringUtils.isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
                    .append("\n\n");

        }
        result.append("Співпадіння\n\n");
        for (Ordered ordered : createdList) {
            for (CanceledOrderReason canceledOrderReason : toFind) {
                if (formInside(canceledOrderReason.getOrdered().getPostComment()) == formInside(ordered.getPostComment()) &&
                        compareShoeArrays(canceledOrderReason.getOrdered().getOrderedShoes(), ordered.getOrderedShoes()) &&
                        ordered.getSize().equals(canceledOrderReason.getOrdered().getSize()) &&
                        used.add(canceledOrderReason)) {
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
        if (toSave.size() > 0) {
            orderService.getOrderRepository().saveAll(toSave);
        }
        String resultString = result.toString();
        googleDocsService.updateReturningsFile(resultString);
        return new StringResponse(resultString);
    }

    public void updateCanceled() {
        List<Ordered> canceledAndDeniedOrders = orderService.getOrderRepository()
                .findAllByStatusInAndLastModifiedDateGreaterThan(Arrays.asList(Status.ВИДАЛЕНО, Status.ВІДМОВА, Status.ЗМІНА_АДРЕСУ),
                        DateHelper.formLocalDateTimeStartOfTheDay(LocalDateTime.now().minusDays(5)));
        for (Ordered ordered : canceledAndDeniedOrders) {
            CanceledOrderReason canceledOrderReason = getCanceledOrderReasonByOrderId(ordered.getId());
            if (canceledOrderReason == null || !canceledOrderReason.isManual()) {
                orderService.updateStatusByNovaPosta(ordered);
            }
        }
    }


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
                        .append(" ").append(StringUtils.isEmpty(canceledOrderReason.getComment()) ? "" : canceledOrderReason.getComment())
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
