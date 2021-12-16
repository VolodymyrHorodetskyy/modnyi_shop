package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.VariantType;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.repository.VariantsRepository;

import java.util.List;

@Service
public class VariantsService {

    private VariantsRepository variantsRepository;

    public VariantsService(VariantsRepository variantsRepository) {
        this.variantsRepository = variantsRepository;
    }


    public List<Variants> getByType(VariantType type) {
        return variantsRepository.findAllByVariantTypeOrderByOrdering(type);
    }

    public Variants getById(Long id){
        return variantsRepository.findById(id).orElse(null);
    }

    public Variants findInVariants(VariantType type, String value) {
        return variantsRepository.findOneByVariantTypeAndGetting(type, value);
    }
}
