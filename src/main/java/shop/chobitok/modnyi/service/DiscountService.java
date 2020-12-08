package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Discount;
import shop.chobitok.modnyi.repository.DiscountRepository;

import java.util.List;

@Service
public class DiscountService {

    private DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> getAll() {
        return discountRepository.findAll();
    }

    public Discount getById(Long id) {
        if (id != null) {
            return discountRepository.findById(id).orElse(null);
        }
        return null;
    }

}
