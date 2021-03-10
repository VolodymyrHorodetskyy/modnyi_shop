package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.DaySpendRec;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DaySpendRepository extends JpaRepository<DaySpendRec, Long> {

    List<DaySpendRec> findAll(Specification specification);

    DaySpendRec findBySpendDate(LocalDate date);

}
