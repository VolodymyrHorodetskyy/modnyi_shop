package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.DayCosts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DayCostsRepository extends JpaRepository<DayCosts, Long> {

    List<DayCosts>findAllBySpendDateGreaterThanEqualAndSpendDateLessThanEqual(
            LocalDate from, LocalDate to);

}
