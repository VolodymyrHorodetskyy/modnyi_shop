package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.DayCosts;
import shop.chobitok.modnyi.entity.SpendType;
import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.CostsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/AdsSpends")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdsSpendController {

    private final CostsService costsService;

    public AdsSpendController(CostsService costsService) {
        this.costsService = costsService;
    }

    @PostMapping
    public List<DayCosts> saveAdsSpendRec(@RequestBody SaveAdsSpendsRequest saveAdsSpendsRequest) {
        return costsService.addOrEditRecord(saveAdsSpendsRequest);
    }

    @GetMapping("/getFinanceStatsString")
    public StringResponse getFinanceStatsStringResponse(@RequestParam String from, @RequestParam String to) {
        return costsService.getFinanceStatsStringResponse(from, to);
    }

    @GetMapping("/getSpendTypes")
    public List<SpendType> getSpendTypes(){
        return Arrays.asList(SpendType.values());
    }

    @GetMapping("/nowTime")
    public LocalDateTime now(){
        return LocalDateTime.now();
    }
}
