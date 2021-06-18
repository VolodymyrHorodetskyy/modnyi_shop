package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.CancelReason;
import shop.chobitok.modnyi.entity.CanceledOrderReason;
import shop.chobitok.modnyi.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CanceledOrderReasonRepository extends JpaRepository<CanceledOrderReason, Long> {

    CanceledOrderReason findFirstByOrderedId(Long id);

    List<CanceledOrderReason> findAll(Specification specification);

    Page<CanceledOrderReason> findAll(Specification specification, Pageable pageable);

    List<CanceledOrderReason> findByReasonIn(List<CancelReason> canceledOrderReasons);

    CanceledOrderReason findFirstByReturnTtn(String ttn);

    List<CanceledOrderReason> findByLastModifiedDateGreaterThanEqualAndStatus(LocalDateTime localDateTime, Status status);

    List<CanceledOrderReason> findByCreatedDateGreaterThanEqual(LocalDateTime localDateTime);
}
