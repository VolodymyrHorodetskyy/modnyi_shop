package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.CanceledOrderReason;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.CancelOrderWithIdRequest;
import shop.chobitok.modnyi.entity.request.CancelOrderWithOrderRequest;
import shop.chobitok.modnyi.entity.response.GetCanceledResponse;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/CancelOrder")
public class CanceledOrderController {

    private CanceledOrderReasonService canceledOrderReasonService;

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
                                      @RequestParam(required = false) Boolean withoutReason) {
        return canceledOrderReasonService.getAll(page, size, ttn, phoneOrName, manual, withoutReason);
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
    public List<CanceledOrderReason> updateCanceled() {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(10));
        return canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }

}
