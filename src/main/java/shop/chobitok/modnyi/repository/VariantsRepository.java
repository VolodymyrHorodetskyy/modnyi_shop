package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.VariantType;
import shop.chobitok.modnyi.entity.Variants;

import java.util.List;

public interface VariantsRepository extends JpaRepository<Variants, Long> {

    List<Variants> findAllByVariantTypeOrderByOrdering(VariantType type);

    Variants findOneByVariantTypeAndGetting(VariantType type, String getting);
}
