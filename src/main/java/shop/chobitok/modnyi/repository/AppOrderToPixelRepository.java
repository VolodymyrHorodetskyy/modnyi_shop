package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrderToPixel;

import java.time.LocalDateTime;
import java.util.List;

public interface AppOrderToPixelRepository extends JpaRepository<AppOrderToPixel, Long> {

    List<AppOrderToPixel> findAllBySentFalse();

    AppOrderToPixel findFirstByAppOrderId(Long id);

    List<AppOrderToPixel> findAllBySentFalseAndAppOrderDomain(String domain);

    List<AppOrderToPixel> findAllByCreatedDateGreaterThanEqual(LocalDateTime from);

    List<AppOrderToPixel> findAllBySentFalseAndTrying(int trying);
}
