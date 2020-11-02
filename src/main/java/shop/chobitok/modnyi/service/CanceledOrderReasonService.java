package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.CancelReason;
import shop.chobitok.modnyi.entity.CanceledOrderReason;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.CancelOrderWithIdRequest;
import shop.chobitok.modnyi.entity.request.CancelOrderWithOrderRequest;
import shop.chobitok.modnyi.entity.response.GetCanceledResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.novaposta.util.NPHelper;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;
import static shop.chobitok.modnyi.util.StringHelper.removeSpaces;

@Service
public class CanceledOrderReasonService {

    private NovaPostaService novaPostaService;
    private OrderRepository orderRepository;
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    private NovaPostaRepository postaRepository;
    private NPHelper npHelper;
    private MailService mailService;
    private StatusChangeService statusChangeService;

    public CanceledOrderReasonService(NovaPostaService novaPostaService, OrderRepository orderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, NovaPostaRepository postaRepository, NPHelper npHelper, MailService mailService, StatusChangeService statusChangeService) {
        this.novaPostaService = novaPostaService;
        this.orderRepository = orderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.postaRepository = postaRepository;
        this.npHelper = npHelper;
        this.mailService = mailService;
        this.statusChangeService = statusChangeService;
    }

    public Ordered cancelOrder(CancelOrderWithOrderRequest cancelOrderRequest) {
        Ordered ordered = orderRepository.findById(cancelOrderRequest.getOrderId()).orElse(null);
        if (ordered == null) {
            throw new ConflictException("Немає такого замовлення");
        }
        statusChangeService.createRecord(ordered, ordered.getStatus(), Status.ВІДМОВА);
        ordered.setStatus(Status.ВІДМОВА);
        if (ordered.isPayed()) {
            mailService.sendEmail("Було оплачено", ordered.getTtn(), "horodetskyyv@gmail.com");
        }
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
        canceledOrderReason.setManual(true);
        orderRepository.save(ordered);
        canceledOrderReasonRepository.save(canceledOrderReason);
        return ordered;
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
                    updated.add(canceledOrderReason);
                }
            } else if (!StringUtils.isEmpty(canceledOrderReason.getReturnTtn())) {
                canceledOrderReason.setStatus(novaPostaService.getStatusByTTN(canceledOrderReason.getReturnTtn()));
                updated.add(canceledOrderReason);
            }
        }
        return canceledOrderReasonRepository.saveAll(updated);
    }

    public Data getReturnedEntity(String ttn) {
        TrackingEntity trackingEntity = postaRepository.getTrackingByTtn(ttn);
        Data returned = null;
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (!StringUtils.isEmpty(data.getLastCreatedOnTheBasisNumber())) {
                returned = postaRepository.getTracking(data.getLastCreatedOnTheBasisNumber()).getData().get(0);
                if (convertToStatus(returned.getStatusCode()) == Status.ЗМІНА_АДРЕСУ) {
                    returned = postaRepository.getTrackingByTtn(returned.getLastCreatedOnTheBasisNumber()).getData().get(0);
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
        if (canceledOrderReason.isManual()) {
            canceledOrderReason.setReturnTtn(cancelOrderWithIdRequest.getReturnTTN());
        }
        return canceledOrderReasonRepository.save(canceledOrderReason);
    }

    public CanceledOrderReason getCanceledOrderReasonByOrderId(Long orderedId) {
        return canceledOrderReasonRepository.findFirstByOrderedId(orderedId);
    }

    public CanceledOrderReason getById(Long id) {
        return canceledOrderReasonRepository.findById(id).orElse(null);
    }

}
