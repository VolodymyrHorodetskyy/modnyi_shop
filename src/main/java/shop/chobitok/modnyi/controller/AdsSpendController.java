package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.AdsSpendRec;
import shop.chobitok.modnyi.entity.request.SaveAdsSpends;
import shop.chobitok.modnyi.service.AdsSpendsService;
import shop.chobitok.modnyi.service.entity.FinanceStats;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/AdsSpends")
public class AdsSpendController {

    private AdsSpendsService adsSpendsService;

    public AdsSpendController(AdsSpendsService adsSpendsService) {
        this.adsSpendsService = adsSpendsService;
    }

    @PostMapping
    public List<AdsSpendRec> saveAdsSpendRec(@RequestBody SaveAdsSpends saveAdsSpends) {
        return adsSpendsService.addOrEditRecord(saveAdsSpends);
    }

    @GetMapping
    public FinanceStats getFinanceStats(@RequestParam String from, @RequestParam String to){
        return adsSpendsService.getFinanceStats(from, to);
    }

}
