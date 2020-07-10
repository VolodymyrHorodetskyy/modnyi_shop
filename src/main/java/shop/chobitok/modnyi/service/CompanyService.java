package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.request.CreateCompanyRequest;
import shop.chobitok.modnyi.repository.CompanyRepository;

import java.util.List;

@Service
public class CompanyService {

    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(CreateCompanyRequest createCompanyRequest) {
        Company company = new Company(createCompanyRequest.getName());
        return companyRepository.save(company);
    }

    public List<Company> getCompanies(){
        return companyRepository.findAll();
    }

}
