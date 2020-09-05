package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.CancelOrderRequest;
import shop.chobitok.modnyi.entity.response.GetCanceledResponse;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;

@RestController
@CrossOrigin
@RequestMapping("/CancelOrder")
public class CanceledOrderController {

    private CanceledOrderReasonService canceledOrderReasonService;

    public CanceledOrderController(CanceledOrderReasonService canceledOrderReasonService) {
        this.canceledOrderReasonService = canceledOrderReasonService;
    }

    @PatchMapping("/cancelOrder")
    public Ordered cancelOrdered(@RequestBody CancelOrderRequest request) {
        return canceledOrderReasonService.cancelOrder(request);
    }

    @GetMapping
    public GetCanceledResponse getAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String ttn,
                                      @RequestParam(required = false) String phoneOrName, @RequestParam(required = false) Boolean manual) {
        return canceledOrderReasonService.getAll(page, size, ttn, phoneOrName, manual);
    }


}
