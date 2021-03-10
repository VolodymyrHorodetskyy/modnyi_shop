package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.DaySpendRec;
import shop.chobitok.modnyi.entity.SpendType;
import shop.chobitok.modnyi.entity.request.SaveAdsSpends;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.SpendsService;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/AdsSpends")
public class AdsSpendController {

    private SpendsService spendsService;

    public AdsSpendController(SpendsService spendsService) {
        this.spendsService = spendsService;
    }

    @PostMapping
    public List<DaySpendRec> saveAdsSpendRec(@RequestBody SaveAdsSpends saveAdsSpends) {
        return spendsService.addOrEditRecord(saveAdsSpends);
    }
/*
    @GetMapping
    public FinanceStats getFinanceStats(@RequestParam String from, @RequestParam String to) {
        return adsSpendsService.getFinanceStats(from, to);
    }*/

    @GetMapping("/getFinanceStatsString")
    public StringResponse getFinanceStatsStringResponse(@RequestParam String from, @RequestParam String to) {
        return spendsService.getFinanceStatsStringResponse(from, to);
    }

    @GetMapping("/getSpendTypes")
    public List<SpendType> getSpendTypes(){
        return Arrays.asList(SpendType.values());
    }

}
