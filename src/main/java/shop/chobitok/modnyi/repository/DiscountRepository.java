package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
