package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Ordered> findAll(Specification specification);

    Ordered findOneByAvailableTrueAndTtn(String ttn);

    List<Ordered> findAllByAvailableTrueAndNotForDeliveryFileFalseAndStatusOrderByDateCreated(Status status);

    List<Ordered> findAllByAvailableTrueAndStatusIn(List<Status> statuses);

    List<Ordered> findAllByAvailableTrueAndPayedFalseAndStatusIn(List<Status> statuses);

    List<Ordered> findAllByAvailableTrueAndReturnedFalseAndStatus(Status status);

    List<Ordered> findAllByAvailableTrueAndReturnedFalseAndCanceledAfterFalseAndStatus(Status status);

    List<Ordered> findBystatusNP(Integer status);

    List<Ordered> findByNotForDeliveryFileTrue();

    List<Ordered> findByDateCreatedGreaterThanEqualAndDateCreatedLessThanEqual(LocalDateTime dateTime1, LocalDateTime dateTime2);

    List<Ordered> findByDateCreatedIsNull();
}
