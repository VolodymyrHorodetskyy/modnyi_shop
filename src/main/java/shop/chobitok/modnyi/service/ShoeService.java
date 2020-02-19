package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.repository.CompanyRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import java.util.List;

@Service
public class ShoeService {

    private ShoeRepository shoeRepository;

    private CompanyRepository companyRepository;


    public ShoeService(ShoeRepository shoeRepository, CompanyRepository companyRepository) {
        this.shoeRepository = shoeRepository;
        this.companyRepository = companyRepository;
    }

    public List<Shoe> getAll(int page, int size, String model) {
        return shoeRepository.findAll(new ShoeSpecification(model), PageRequest.of(page, size)).getContent();
    }

    public Shoe addShoe(Shoe shoe) {
        Company company = companyRepository.findAll().get(0);
        shoe.setCompany(company);
        return shoeRepository.save(shoe);
    }

}
