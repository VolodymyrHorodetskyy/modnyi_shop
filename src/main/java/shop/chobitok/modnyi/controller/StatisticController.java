package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.service.StatisticService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/statistic")
public class StatisticController {

    private StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public Object get() {
        return statisticService.needToBePayed();
    }

    @GetMapping("/getAllDenied")
    public List<Ordered> getAllDenied(@RequestParam(required = false) boolean returned) {
        return statisticService.getAllDenied(returned);
    }

    @GetMapping("/getProblematic")
    public List<Ordered> getProblematic() {
        return statisticService.getProblematic();
    }


    @GetMapping("/getEarnedMoney")
    public Double getEarnedMoney() {
        return statisticService.getEarnedMoney();
    }

    @GetMapping("/getAllCreatedFromFile")
    public List<String> getAllCreated(@RequestParam String path) {
        return statisticService.formListForDeliveryFromFile(path);
    }

}
