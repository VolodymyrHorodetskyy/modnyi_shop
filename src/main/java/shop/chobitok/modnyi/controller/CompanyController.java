package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.service.CompanyService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/company")
public class CompanyController {

    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public Company createCompany(@RequestBody CreateCompanyRequest createCompanyRequest) {
        return companyService.createCompany(createCompanyRequest);
    }

    @GetMapping
    public List<Company> getCompanies(){
        return companyService.getCompanies();
    }

}
