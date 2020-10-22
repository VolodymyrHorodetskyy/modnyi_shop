package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.entity.StatusChangeRecord;
import shop.chobitok.modnyi.service.StatusChangeService;

import java.util.List;

@RestController
@RequestMapping("/OrderStatusChange")
public class OrderStatusChangeController {

    private StatusChangeService statusChangeService;


    public OrderStatusChangeController(StatusChangeService statusChangeService) {
        this.statusChangeService = statusChangeService;
    }

    @GetMapping
    public List<StatusChangeRecord> getAll() {
        return statusChangeService.getAll();
    }


}
