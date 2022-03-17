package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.FinanceService;
import shop.chobitok.modnyi.util.StringHelper;

@RestController
@CrossOrigin
@RequestMapping("/finance")
@PreAuthorize("hasAuthority('ADMIN')")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/getEarning")
    public EarningsResponse getEarningsResponse(@RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        return financeService.getEarnings(from, to);
    }

    @GetMapping("/getEarningString")
    public StringResponse getEarningsResponseString(@RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        return StringHelper.fromEarningResponse(financeService.getEarnings(from, to));
    }


}
