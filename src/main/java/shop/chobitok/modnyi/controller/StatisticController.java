package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.StatisticService;

@RestController
@CrossOrigin
@RequestMapping("/statistic")
public class StatisticController {

    private StatisticService statisticService;
    private OrderService orderService;


    public StatisticController(StatisticService statisticService, OrderService orderService) {
        this.statisticService = statisticService;
        this.orderService = orderService;
    }

    @GetMapping("/needToPayed")
    public Object get(@RequestParam String pathToAllTTNfile, @RequestParam String pathToPayedTTNFile) {
        return statisticService.needToBePayed(pathToAllTTNfile, pathToPayedTTNFile);
    }

    @GetMapping("/getAllReceivedAndDeniedCount")
    public String getReceivedAndDeniedCount(@RequestParam String path) {
        return statisticService.countAllReceivedAndDenied(path);
    }

    @GetMapping("/getAllDenied")
    public String getAllDenied(@RequestParam String pathAllTTNFile, @RequestParam(required = false) boolean returned) {
        return statisticService.getAllDenied(pathAllTTNFile, returned);
    }

/*    @GetMapping("/needDelivery")
    public String needDelivery(@RequestParam String path){
        return statisticService.countNeedDelivery(path);
    }*/

    @GetMapping("/needDeliveryFromDB")
    public StringResponse needDelivery(@RequestParam(required = false) boolean updateStatuses) {
        return statisticService.countNeedDeliveryFromDB(updateStatuses);
    }

    @GetMapping("/getIssueOrdered")
    public StringResponse getIssueOrders() {
        return statisticService.getIssueOrders();
    }

    @PostMapping("/needToPayedFromFile")
    public StringResponse needToPayed(@RequestParam(required = false) boolean updateStatuses, @RequestParam MultipartFile file) {
        return statisticService.needToPayed(updateStatuses, file);
    }

    @GetMapping("/returned")
    public StringResponse returned(@RequestParam(required = false) boolean setNotForDelivery) {
        return statisticService.getReturned(setNotForDelivery);
    }

    @GetMapping("/canceled")
    public StringResponse canceled(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.getCanceledString(updateStatuses);
    }

}
