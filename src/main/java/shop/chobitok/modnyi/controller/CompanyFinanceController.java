package shop.chobitok.modnyi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.CompanyFinanceControl;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.service.CompanyFinanceControlService;

import java.util.List;

@RequestMapping("/companyFinance")
@RestController
@CrossOrigin
@PreAuthorize("hasAuthority('ADMIN')")
public class CompanyFinanceController {

    @Autowired
    private CompanyFinanceControlService service;

    public CompanyFinanceController(CompanyFinanceControlService service) {
        this.service = service;
    }

    @PostMapping
    public CompanyFinanceControl doOperation(@RequestBody DoCompanyFinanceControlOperationRequest request) {
        return service.doOperation(request);
    }

    @GetMapping
    public List<CompanyFinanceControl> getLastOperationByCompanyId(@RequestParam Long companyId, @RequestParam int size) {
        return service.getLastCompanyFinanceControlByCompanyId(companyId, size);
    }
}
