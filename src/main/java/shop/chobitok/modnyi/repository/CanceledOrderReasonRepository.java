package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.CanceledOrderReason;

@Repository
public interface CanceledOrderReasonRepository extends JpaRepository<CanceledOrderReason, Long> {
}
