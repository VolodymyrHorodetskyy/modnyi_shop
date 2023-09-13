package shop.chobitok.modnyi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.response.IncomeReport;
import shop.chobitok.modnyi.service.IncomeCalculatorService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/income")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
@CrossOrigin
public class IncomeController {

    private final IncomeCalculatorService incomeCalculatorService;

    public IncomeController(IncomeCalculatorService incomeCalculatorService) {
        this.incomeCalculatorService = incomeCalculatorService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<IncomeReport> calculateIncome(@RequestParam String from, @RequestParam String to,
                                                        @RequestParam(required = false) boolean showOrderDetails,
                                                        @RequestParam(required = false) boolean showCostsDetails) {
        LocalDateTime fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
        IncomeReport report = incomeCalculatorService.calculateIncome(fromDate, toDate, showOrderDetails, showCostsDetails);
        return ResponseEntity.ok(report);
    }

}

