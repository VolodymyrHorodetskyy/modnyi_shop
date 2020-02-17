package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Shoe;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {

    Page<Shoe> findAll(Specification specification, Pageable pageable);

}
