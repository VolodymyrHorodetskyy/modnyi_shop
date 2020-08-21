package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.ShoePrice;

import java.util.List;

public interface ShoePriceRepository extends JpaRepository<ShoePrice, Long> {

    ShoePrice findOneByToDateIsNullAndShoeId(Long shoeId);

    List<ShoePrice> findByShoeId(Long shoeId, Sort sort);

    ShoePrice findTopByShoeId(Long shoeId, Sort sort);

}
