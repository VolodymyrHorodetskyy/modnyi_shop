package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.WholesaleOrder;

public interface WholesaleRepository extends JpaRepository<WholesaleOrder, Long> {
}
