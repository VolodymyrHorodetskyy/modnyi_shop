package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.WholesaleOrder;
import shop.chobitok.modnyi.entity.request.AddWholesaleOrderRequest;
import shop.chobitok.modnyi.service.WholesaleService;

@RestController
@CrossOrigin
@RequestMapping("/wholesale")
@PreAuthorize("hasAuthority('ADMIN')")
public class WholesaleController {

    private final WholesaleService wholesaleService;

    public WholesaleController(WholesaleService wholesaleService) {
        this.wholesaleService = wholesaleService;
    }

    @PostMapping
    public WholesaleOrder saveWholesaleOrder(@RequestBody AddWholesaleOrderRequest request) {
        return wholesaleService.saveWholesaleOrder(request);
    }

    @PatchMapping("/makeCompleted")
    public void makeCompleted(@RequestParam Long wholesaleOrderId) {
        wholesaleService.addToCompanyFinanceControl(wholesaleOrderId);
    }
}
