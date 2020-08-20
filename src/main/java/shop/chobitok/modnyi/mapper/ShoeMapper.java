package shop.chobitok.modnyi.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.CreateShoeRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.CompanyRepository;
import shop.chobitok.modnyi.service.CompanyService;

@Service
public class ShoeMapper {

    private CompanyRepository companyRepository;

    public ShoeMapper(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Shoe convertFromCreateShoeRequest(CreateShoeRequest createShoeRequest) {
        Shoe shoe = null;
        if (createShoeRequest != null) {
            shoe = new Shoe();
            shoe = setToShoe(createShoeRequest, shoe);
        }
        return shoe;
    }

    public Shoe convertFromCreateShoeRequest(CreateShoeRequest createShoeRequest, Shoe shoe) {
        return setToShoe(createShoeRequest, shoe);
    }

    private Shoe setToShoe(CreateShoeRequest createShoeRequest, Shoe shoe) {
        Company company = companyRepository.getOne(createShoeRequest.getCompanyId());
        if (company == null) {
            throw new ConflictException("Company not found");
        }
        shoe.setCompany(company);
        shoe.setName(createShoeRequest.getName());
        shoe.setModel(createShoeRequest.getModel());
        shoe.setColor(createShoeRequest.getColor());
        shoe.setDescription(createShoeRequest.getDescription());
        return shoe;
    }

}
