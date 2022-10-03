package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AppOrder;
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

    private final AppOrderService appOrderService;
    private final CheckerService checkerService;

    public AppOrderController(AppOrderService appOrderService, CheckerService checkerService) {
        this.appOrderService = appOrderService;
        this.checkerService = checkerService;
    }

    @PostMapping("/catchOrder")
    public AppOrder webhook(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @PostMapping("/catchOrder2")
    public AppOrder webhook2(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @PostMapping("/catchOrder3")
    public AppOrder webhook3(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @PostMapping("/catchOrder4")
    public AppOrder webhook4(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @PostMapping("/catchOrder5")
    public AppOrder webhook5(@RequestBody String s) {
        return appOrderService.catchOrder(s);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public Map<AppOrderStatus, Set<AppOrder>> getAll(Long id, String phoneAndName, String comment, String fromForNotReady, String fromForReady, String userId) {
        return appOrderService.getAll(id, phoneAndName, comment, fromForNotReady, fromForReady, userId);
    }

    @PatchMapping("/changeStatusAndComment")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ChangeAppOrderResponse changeStatus(@RequestBody ChangeAppOrderRequest request) {
        return appOrderService.changeAppOrder(request);
    }

    @GetMapping("statuses")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
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

    @PatchMapping("makeAppOrdersNewAgain")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public void makeAppOrdersNewAgain() {
        checkerService.makeAppOrderNewAgain();
    }
}
