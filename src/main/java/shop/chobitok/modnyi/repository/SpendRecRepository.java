package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.SpendRec;

@Repository
public interface SpendRecRepository extends JpaRepository<SpendRec, Long> {
}
