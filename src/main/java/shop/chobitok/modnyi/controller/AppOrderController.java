package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.request.AddCommentToAppOrderRequest;
import shop.chobitok.modnyi.entity.request.ChangeAppOrderStatusAndCommentRequest;
import shop.chobitok.modnyi.service.AppOrderService;

import java.time.LocalDateTime;
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
    public Map<AppOrderStatus, List<AppOrder>> getAll(Long id, String phoneAndName, String from) {
        return appOrderService.getAll(id, phoneAndName, from);
    }

    @PatchMapping("/changeStatusAndComment")
    public AppOrder changeStatus(@RequestBody ChangeAppOrderStatusAndCommentRequest request) {
        return appOrderService.changeAppOrderStatus(request);
    }

    @GetMapping("/statuses")
    public List<AppOrderStatus> getStatus() {
        return Arrays.asList(AppOrderStatus.values());
    }

    @PatchMapping("/addCommnet")
    public AppOrder addComment(@RequestBody AddCommentToAppOrderRequest request) {
        return appOrderService.addComment(request);
    }

}
