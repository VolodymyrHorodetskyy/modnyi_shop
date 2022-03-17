package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AppOrderCancellationReason;
import shop.chobitok.modnyi.entity.CanceledOrderReason;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.CancelOrderWithIdRequest;
import shop.chobitok.modnyi.entity.request.CancelOrderWithOrderRequest;
import shop.chobitok.modnyi.entity.response.GetCanceledResponse;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

@RestController
@CrossOrigin
@RequestMapping("/CancelOrder")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class CanceledOrderController {

    private final CanceledOrderReasonService canceledOrderReasonService;

    public CanceledOrderController(CanceledOrderReasonService canceledOrderReasonService) {
        this.canceledOrderReasonService = canceledOrderReasonService;
    }

    @PatchMapping("/cancelOrder")
    public Ordered cancelOrdered(@RequestBody CancelOrderWithOrderRequest request) {
        return canceledOrderReasonService.cancelOrder(request);
    }

    @PatchMapping("/setReason")
    public CanceledOrderReason setReason(@RequestBody CancelOrderWithIdRequest cancelOrderWithIdRequest) {
        return canceledOrderReasonService.setReason(cancelOrderWithIdRequest);
    }

    @GetMapping
    public GetCanceledResponse getAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String ttn,
                                      @RequestParam(required = false) String phoneOrName, @RequestParam(required = false) Boolean manual,
                                      @RequestParam(required = false) Boolean withoutReason, @RequestParam(required = false) String userId) {
        return canceledOrderReasonService.getAll(page, size, ttn, phoneOrName, manual, withoutReason, userId);
    }

    @GetMapping("/getCanceledOrderByOrderId")
    public CanceledOrderReason getCancelOrderById(@RequestParam Long id) {
        return canceledOrderReasonService.getCanceledOrderReasonByOrderId(id);
    }

    @GetMapping("/getCanceledOrder")
    public CanceledOrderReason getCancelOrder(@RequestParam Long id) {
        return canceledOrderReasonService.getById(id);
    }

    @PutMapping("/updateCanceled")
    public List<CanceledOrderReason> updateCanceled(@RequestParam long days) {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(days));
        return canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }

    @GetMapping("getAppOrderCancellationReasons")
    public List<AppOrderCancellationReason> getAppOrderCancellationReasons() {
        return asList(AppOrderCancellationReason.values());
    }
}
