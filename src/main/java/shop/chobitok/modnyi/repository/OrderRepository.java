package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Ordered, Long> {

    Page<Ordered> findAll(Specification specification, Pageable pageable);

    List<Ordered> findAll(Specification specification, Sort sort);

    List<Ordered> findAll(Specification specification);

    Ordered findOneByAvailableTrueAndTtn(String ttn);

    List<Ordered> findAllByAvailableTrueAndNotForDeliveryFileFalseAndStatusOrderByDateCreated(Status status);

    List<Ordered> findAllByAvailableTrueAndStatusIn(List<Status> statuses);

    List<Ordered> findAllByStatusInAndLastModifiedDateGreaterThan(List<Status> statuses, LocalDateTime dateTime);

    List<Ordered> findAllByStatusInAndCreatedDateGreaterThan(List<Status> statuses, LocalDateTime dateTime);

    List<Ordered> findAllByStatusInAndDateCreatedGreaterThanAndDateCreatedLessThanAndNpAccountId(List<Status> statuses, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Long npAccountId);

    List<Ordered> findAllByAvailableTrueAndStatusInOrderByUrgentDesc(List<Status> statuses);

    List<Ordered> findAllByAvailableTrueAndPayedFalseAndStatusIn(List<Status> statuses);

    List<Ordered> findByCreatedDateGreaterThan(LocalDateTime dateTime);

    List<Ordered> findAllByAvailableTrueAndReturnedFalseAndCanceledAfterFalseAndStatus(Status status);

    List<Ordered> findBystatusNP(Integer status);

    List<Ordered> findByNotForDeliveryFileTrue();

    List<Ordered> findByClientPhone(String phone);

    List<Ordered> findByClientId(Long id);

    List<Ordered> findAllByAvailableTrueAndUserId(Long id);

    List<Ordered> findAllByAvailableTrueAndUserIdAndStatus(Long id, Status status);

    List<Ordered> findByNpAccountId(Long npAccountId);

    List<Ordered> findByCardId(Long id);

}
