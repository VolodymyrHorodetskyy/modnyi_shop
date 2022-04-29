package shop.chobitok.modnyi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.CompanyFinanceControl;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.CompanyFinanceControlService;

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
    public StringResponse getLastOperationByCompanyId(@RequestParam Long companyId) {
        return service.getLastCompanyFinanceControlByCompanyIdString(companyId);
    }
}
