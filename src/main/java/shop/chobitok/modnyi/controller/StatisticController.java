package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.StatisticService;

@RestController
@CrossOrigin
@RequestMapping("/statistic")
public class StatisticController {

    private StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
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
    public StringResponse needDelivery(){
        return statisticService.countNeedDeliveryFromDB();
    }
/*
    @PostMapping("/needDeliverymulti")
    public String needDelivery(@RequestParam MultipartFile file)
    {
        return statisticService.countNeedDelivery(file);
    }*/

}
