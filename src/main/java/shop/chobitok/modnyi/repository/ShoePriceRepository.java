package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.ShoePrice;

public interface ShoePriceRepository extends JpaRepository<ShoePrice, Long> {

    ShoePrice findOneByToIsNullAndShoeId(Long shoeId);

}
