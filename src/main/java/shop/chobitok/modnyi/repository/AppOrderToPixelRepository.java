package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrderToPixel;

import java.util.List;

public interface AppOrderToPixelRepository extends JpaRepository<AppOrderToPixel, Long> {

    List<AppOrderToPixel> findAllBySentFalse();

    AppOrderToPixel findOneByAppOrderId(Long id);

    List<AppOrderToPixel> findAllBySentFalseAndAppOrderDomain(String domain);

}
