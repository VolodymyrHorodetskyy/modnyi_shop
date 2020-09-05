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
import shop.chobitok.modnyi.entity.request.CancelOrderRequest;
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
import shop.chobitok.modnyi.util.StringHelper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public CanceledOrderReasonService(NovaPostaService novaPostaService, OrderRepository orderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, NovaPostaRepository postaRepository, NPHelper npHelper) {
        this.novaPostaService = novaPostaService;
        this.orderRepository = orderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.postaRepository = postaRepository;
        this.npHelper = npHelper;
    }

    @Transactional
    public Ordered cancelOrder(CancelOrderRequest cancelOrderRequest) {
        Ordered ordered = orderRepository.getOne(cancelOrderRequest.getOrderId());
        if (ordered == null) {
            throw new ConflictException("Немає такого замовлення");
        }
        ordered.setStatus(Status.ВІДМОВА);
        CanceledOrderReason canceledOrderReason = canceledOrderReasonRepository.findFirstByOrderedId(cancelOrderRequest.getOrderId());
        if (canceledOrderReason == null) {
            canceledOrderReason = new CanceledOrderReason(ordered, cancelOrderRequest.getReason(), cancelOrderRequest.getComment(),
                    cancelOrderRequest.getNewTTN());
        } else {
            canceledOrderReason.setComment(cancelOrderRequest.getComment());
            canceledOrderReason.setReason(cancelOrderRequest.getReason());
            canceledOrderReason.setNewTtn(cancelOrderRequest.getNewTTN());
        }
        canceledOrderReason.setManual(true);
        canceledOrderReasonRepository.save(canceledOrderReason);
        orderRepository.save(ordered);
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

    public List<CanceledOrderReason> checkIfWithoutStatusExistsAndSetReturnTtnAndStatus() {
        List<Ordered> orderedList = orderRepository.findAllByAvailableTrueAndStatusIn(Arrays.asList(Status.ВІДМОВА));
        List<CanceledOrderReason> canceledOrderReasons = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            if (canceledOrderReasonRepository.findFirstByOrderedId(ordered.getId()) == null) {
                canceledOrderReasons.add(createDefaultReasonOnCancel(ordered));
            }
        }

        return canceledOrderReasons;
    }

    public List<CanceledOrderReason> setReturnTtnAndStatus() {
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusMonths(1), true));
        List<CanceledOrderReason> updated = new ArrayList<>();
        for (CanceledOrderReason canceledOrderReason : canceledOrderReasons) {
            if (StringUtils.isEmpty(canceledOrderReason.getReturnTtn()) && canceledOrderReason.getOrdered() != null
                    && !StringUtils.isEmpty(canceledOrderReason.getOrdered().getTtn())) {
                Data returned = getReturnedEntity(canceledOrderReason.getOrdered().getTtn());
                if (returned != null) {
                    canceledOrderReason.setReturnTtn(returned.getNumber());
                    canceledOrderReason.setStatus(convertToStatus(returned.getStatusCode()));
                    updated.add(canceledOrderReason);
                }
            } else {
                canceledOrderReason.setStatus(novaPostaService.getStatus(canceledOrderReason.getReturnTtn()));
                updated.add(canceledOrderReason);
            }
        }
        return canceledOrderReasonRepository.saveAll(updated);
    }

    public Data getReturnedEntity(String ttn) {
        TrackingEntity trackingEntity = postaRepository.getTracking(npHelper.formGetTrackingRequest(ttn));
        Data returned = null;
        if (trackingEntity != null && trackingEntity.getData().size() > 0) {
            Data data = trackingEntity.getData().get(0);
            if (StringUtils.isEmpty(data.getLastCreatedOnTheBasisNumber())) {
                //TODO: Make not returned
            } else {
                returned = postaRepository.getTracking(npHelper.formGetTrackingRequest(data.getLastCreatedOnTheBasisNumber())).getData().get(0);
                if (convertToStatus(returned.getStatusCode()) == Status.ЗМІНА_АДРЕСУ) {
                    returned = postaRepository.getTracking(npHelper.formGetTrackingRequest(returned.getLastCreatedOnTheBasisNumber())).getData().get(0);
                }
            }
        }
        return returned;
    }


    public GetCanceledResponse getAll(int page, int size, String ttn, String phoneOrName, Boolean manual) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reason").and(Sort.by(Sort.Direction.DESC, "createdDate")));
        Page page1 = canceledOrderReasonRepository.findAll(new CanceledOrderReasonSpecification(LocalDateTime.now().minusMonths(1), false, removeSpaces(ttn), phoneOrName, manual), pageRequest);
        return new GetCanceledResponse(page1.getContent(), page1.getTotalElements());
    }

}
