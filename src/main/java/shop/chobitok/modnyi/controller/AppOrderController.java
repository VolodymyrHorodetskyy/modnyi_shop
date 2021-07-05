package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderProcessing;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderRequest;
import shop.chobitok.modnyi.entity.response.ChangeAppOrderResponse;
import shop.chobitok.modnyi.service.AppOrderService;
import shop.chobitok.modnyi.service.CheckerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/AppOrder")
public class AppOrderController {

    private AppOrderService appOrderService;
    private CheckerService checkerService;

    public AppOrderController(AppOrderService appOrderService, CheckerService checkerService) {
        this.appOrderService = appOrderService;
        this.checkerService = checkerService;
    }

    @PostMapping("/catchOrder")
    public AppOrder webhook(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @GetMapping
    public Map<AppOrderStatus, Set<AppOrder>> getAll(Long id, String phoneAndName, String comment, String fromForNotReady, String fromForReady, String userId) {
        return appOrderService.getAll(id, phoneAndName, comment, fromForNotReady, fromForReady, userId);
    }

    @PatchMapping("/changeStatusAndComment")
    public ChangeAppOrderResponse changeStatus(@RequestBody ChangeAppOrderRequest request) {
        return appOrderService.changeAppOrder(request);
    }

    @GetMapping("/statuses")
    public List<AppOrderStatus> getStatus() {
        List<AppOrderStatus> appOrderStatuses = new ArrayList<>();
        appOrderStatuses.add(AppOrderStatus.Новий);
        appOrderStatuses.add(AppOrderStatus.В_обробці);
        for (AppOrderStatus appOrderStatus : AppOrderStatus.values()) {
            if (!appOrderStatuses.contains(appOrderStatus)) {
                appOrderStatuses.add(appOrderStatus);
            }
        }
        return appOrderStatuses;
    }

    @GetMapping("getAppOrderProcessingHistory")
    List<AppOrderProcessing> getAppOrderProcessingHistory(@RequestParam String fromDate) {
        return appOrderService.getAllAppOrderProcessingsFromDate(fromDate);
    }

    @PatchMapping("makeAppOrdersNewAgain")
    public void makeAppOrdersNewAgain() {
        checkerService.makeAppOrderNewAgain();

    }


}
