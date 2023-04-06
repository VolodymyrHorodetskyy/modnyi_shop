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

    List<AppOrder> findByStatusInOrderByCreatedDateDesc(List<AppOrderStatus> statuses);

    List<AppOrder> findByPreviousStatus(AppOrderStatus status);

    List<AppOrder> findByCreatedDateLessThanAndStatusIn(LocalDateTime localDateTime, List<AppOrderStatus> statuses);

    List<AppOrder> findByTtnIsNotNullAndLastModifiedDateIsGreaterThan(LocalDateTime dateTime);

    List<AppOrder> findByCreatedDateGreaterThanEqualOrderByCreatedDateDesc(LocalDateTime from);

    List<AppOrder> findAllByStatusInAndUserId(List<AppOrderStatus> statuses, Long id);

    AppOrder findFirstByStatusInAndUserIdOrderByCreatedDateDesc(List<AppOrderStatus> statuses, Long id);

    AppOrder findFirstByStatusInAndPreviousStatusIsNullAndUserIdOrderByDateAppOrderShouldBeProcessedDesc(List<AppOrderStatus> statuses, Long id);

    AppOrder findOneByTtn(String ttn);

    AppOrder findFirstByOrderByCreatedDateDesc();

    AppOrder findFirstByDateAppOrderShouldBeProcessedGreaterThanEqualAndUserId(LocalDateTime from,
                                                                               Long id);
    AppOrder findByTtn(String ttn);

    List<AppOrder> findByRemindOnIsLessThanEqual(LocalDateTime localDateTime);

    List<AppOrder> findByDataParsedFalse();

    List<AppOrder> findByCreatedDateGreaterThanEqualAndDomainIsNull(LocalDateTime dateTime);

    AppOrder findByHoroshopOrderId(Long id);
}
