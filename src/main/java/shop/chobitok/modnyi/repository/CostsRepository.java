package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Costs;
import shop.chobitok.modnyi.entity.Variants;

import java.time.LocalDateTime;

@Repository
public interface CostsRepository extends JpaRepository<Costs, Long> {
    Page<Costs> findByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqualAndSpendType(
            LocalDateTime start,
            LocalDateTime end,
            Variants spendType,
            Pageable pageable);

    Page<Costs> findByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);
}
