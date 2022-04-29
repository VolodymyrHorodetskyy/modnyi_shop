package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.CompanyFinanceControl;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.CompanyFinanceControlRepository;
import shop.chobitok.modnyi.util.StringHelper;

@Service
public class CompanyFinanceControlService {

    private final CompanyFinanceControlRepository companyFinanceControlRepository;
    private final CompanyService companyService;

    public CompanyFinanceControlService(CompanyFinanceControlRepository companyFinanceControlRepository, CompanyService companyService) {
        this.companyFinanceControlRepository = companyFinanceControlRepository;
        this.companyService = companyService;
    }

    public CompanyFinanceControl doOperation(DoCompanyFinanceControlOperationRequest request) {
        Company company = companyService.getCompany(request.getCompanyId());
        CompanyFinanceControl companyFinanceControl = getLastCompanyFinanceControlByCompanyId(request.getCompanyId());
        if (company != null) {
            return companyFinanceControlRepository.save(new CompanyFinanceControl(request,
                    companyFinanceControl.getCurrentFinanceState() + request.getOperation(),
                    company));
        } else {
            throw new ConflictException("Company not found");
        }
    }

    public StringResponse getLastCompanyFinanceControlByCompanyIdString(Long companyId) {
        return StringHelper.fromCompanyFinanceControl(companyFinanceControlRepository.findOneByCompanyIdOrderByCreatedDateDesc(companyId));
    }

    public CompanyFinanceControl getLastCompanyFinanceControlByCompanyId(Long companyId) {
        return companyFinanceControlRepository.findOneByCompanyIdOrderByCreatedDateDesc(companyId);
    }
}
