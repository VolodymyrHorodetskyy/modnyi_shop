package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrderProcessing;
import shop.chobitok.modnyi.entity.AppOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppOrderProcessingRepository extends JpaRepository<AppOrderProcessing, Long> {

    List<AppOrderProcessing> findAllByCreatedDateGreaterThanEqualAndOldStatusOrderByCreatedDateDesc(LocalDateTime from, AppOrderStatus status);

    AppOrderProcessing findFirstByUserIdAndLastModifiedDateGreaterThanOrderByLastModifiedDateDesc(Long id,
                                                                                                  LocalDateTime from);

    List<AppOrderProcessing> findByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(LocalDateTime from, LocalDateTime to);
}
