package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {

    List<AppOrder> findAll(Specification specification, Sort sort);

    List<AppOrder> findAll(Specification specification);

    List<AppOrder> findByStatusIn(List<AppOrderStatus> statuses);

    List<AppOrder> findByPreviousStatus(AppOrderStatus status);

    List<AppOrder> findByTtnIsNotNullAndLastModifiedDateIsGreaterThan(LocalDateTime dateTime);

    AppOrder findOneByTtn(String ttn);

}
