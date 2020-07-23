package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrder;

import java.util.List;

public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {

    List<AppOrder> findAll(Specification specification, Sort sort);

}
