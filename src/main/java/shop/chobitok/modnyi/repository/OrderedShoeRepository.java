package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.OrderedShoe;

import java.util.List;

public interface OrderedShoeRepository extends JpaRepository<OrderedShoe, Long> {

    List<OrderedShoe> findAllByPayedFalseAndShoeCompanyId(Long companyId);
    List<OrderedShoe> findAllByPayedFalse();
}
