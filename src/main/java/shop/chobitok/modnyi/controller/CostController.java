package shop.chobitok.modnyi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Costs;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.service.CostsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/costs")
@CrossOrigin
@PreAuthorize("hasAuthority('ADMIN')")
public class CostController {

    @Autowired
    private CostsService costsService;

    @GetMapping
    public List<Costs> getCosts(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) Variants spendType) {
        return costsService.findByDateRangeAndSpendType(LocalDateTime.parse(start + "T00:00:00"), LocalDateTime.parse(end + "T23:59:59"), spendType);
    }
}
