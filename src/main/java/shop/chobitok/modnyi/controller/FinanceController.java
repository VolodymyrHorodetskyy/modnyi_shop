package shop.chobitok.modnyi.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.service.FinanceService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin
@RequestMapping("/finance")
public class FinanceController {

    private FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/getEarning")
    public EarningsResponse getEarningsResponse(@RequestParam String from, @RequestParam String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return financeService.getEarnings(LocalDateTime.parse(from, formatter), LocalDateTime.parse(to, formatter));
    }


}
