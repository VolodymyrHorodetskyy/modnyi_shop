package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrderProcessing;

import java.time.LocalDateTime;
import java.util.List;

public interface AppOrderProcessingRepository extends JpaRepository<AppOrderProcessing, Long> {

    List<AppOrderProcessing> findAllByCreatedDateGreaterThanEqual(LocalDateTime from, Sort sort);

}
