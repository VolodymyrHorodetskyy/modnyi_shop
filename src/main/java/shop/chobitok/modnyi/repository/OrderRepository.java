package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Ordered;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Ordered, Long> {

    List<Ordered> findAll(Specification specification, Pageable pageable);
}
