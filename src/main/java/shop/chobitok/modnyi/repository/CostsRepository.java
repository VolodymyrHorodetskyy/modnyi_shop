package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Costs;

@Repository
public interface CostsRepository extends JpaRepository<Costs, Long> {
}
