package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    List<Ordered> findAllByWithoutTTNFalseAndStatusIn(List<Status> statuses);

    List<Ordered> findAllByStatusInAndDatePayedKeepingNPIsNotNull(List<Status> statuses);

    List<Ordered> findAllByStatusInAndCreatedDateGreaterThan(List<Status> statuses, LocalDateTime dateTime);

    List<Ordered> findAllByStatusInAndDateCreatedGreaterThanAndDateCreatedLessThanAndNpAccountId(List<Status> statuses, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Long npAccountId);

    List<Ordered> findAllByAvailableTrueAndPayedFalseAndStatusIn(List<Status> statuses);

    List<Ordered> findByCreatedDateGreaterThanAndCityIsNull(LocalDateTime dateTime);

    List<Ordered> findBystatusNP(Integer status);

    List<Ordered> findByClientId(Long id);

    List<Ordered> findAllByAvailableTrueAndUserIdAndStatusAndPayedForUserFalse(Long id, Status status);

    List<Ordered> findByCardIdAndNpAccountIdAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(Long id, Long npAccountId, LocalDateTime from, LocalDateTime to);

    @Query(value = "Select max(o.id) from ordered o", nativeQuery = true)
    Integer findMaximum();

    Ordered findFirstByOrderedShoeListIdIn(Long orderedShoeId);
    List<Ordered>findAllByCreatedDateGreaterThanEqual(LocalDateTime date);

    @Query(value =
            "select * from ordered a join ordered_ordered_shoe_list b on a.id = b.ordered_id join ordered_shoe c on b.ordered_shoe_list_id = c.id where c.id = ?1",
            nativeQuery = true)
    Ordered findByOrderedShoeId(Long orderedShoeId);
}
