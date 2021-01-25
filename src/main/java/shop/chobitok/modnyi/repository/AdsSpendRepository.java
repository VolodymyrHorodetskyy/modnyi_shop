package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.AdsSpendRec;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdsSpendRepository extends JpaRepository<AdsSpendRec, Long> {

    List<AdsSpendRec> findAll(Specification specification);

    AdsSpendRec findBySpendDate(LocalDate date);

}
