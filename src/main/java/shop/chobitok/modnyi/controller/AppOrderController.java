package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.service.AppOrderService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/AppOrder")
public class AppOrderController {

    private AppOrderService appOrderService;

    public AppOrderController(AppOrderService appOrderService) {
        this.appOrderService = appOrderService;
    }

    @PostMapping("/catchOrder")
    public AppOrder webhook(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @GetMapping
    public Map<AppOrderStatus, List<AppOrder>> getAll(Long id, String phoneAndName, String fromForNotReady, String fromForReady) {
        return appOrderService.getAll(id, phoneAndName, fromForNotReady, fromForReady);
    }

    @PatchMapping("/changeStatusAndComment")
    public ChangeAppOrderResponse changeStatus(@RequestBody ChangeAppOrderRequest request) {
        return appOrderService.changeAppOrder(request);
    }

    @GetMapping("/statuses")
    public List<AppOrderStatus> getStatus() {
        return Arrays.asList(AppOrderStatus.values());
    }


}
