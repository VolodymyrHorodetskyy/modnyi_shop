package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.chobitok.modnyi.entity.OrderedShoe;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderedShoeRepository extends JpaRepository<OrderedShoe, Long> {

    @Query(value =
            "SELECT * FROM ordered_shoe os JOIN ordered_ordered_shoe_list oosl ON os.id = oosl.ordered_shoe_list_id JOIN ordered o ON o.id = oosl.ordered_id JOIN shoe s ON s.id = os.shoe_id WHERE o.status = 3 AND s.company_id = ?1 and os.payed = 0;", nativeQuery = true)
    List<OrderedShoe> findAllByStatusReceivedAndPayedFalseAndCompanyId(Long companyId);

    @Query(value =
            "SELECT * FROM ordered_shoe os JOIN ordered_ordered_shoe_list oosl ON os.id = oosl.ordered_shoe_list_id JOIN ordered o ON o.id = oosl.ordered_id JOIN shoe s ON s.id = os.shoe_id WHERE o.status != 0 and o.status != 5 and o.status != 7 AND s.company_id = ?1 and os.payed = 0;", nativeQuery = true)
    List<OrderedShoe> findAllByStatusNotCreatedAndPayedFalseAndCompanyId(Long companyId);

    List<OrderedShoe> findAllByPayedFalse();

    List<OrderedShoe> findAllByShoeCompanyId(Long companyId);

    List<OrderedShoe> findAllByPayedFalseAndCreatedDateLessThanEqual(LocalDateTime from);

    List<OrderedShoe> findAllByCreatedDateGreaterThanEqualAndShoeCompanyId(LocalDateTime from, Long companyId);
}