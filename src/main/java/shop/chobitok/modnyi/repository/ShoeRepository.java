package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Shoe;

import java.util.List;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {

    Page<Shoe> findAll(Specification specification, Pageable pageable);

    List<Shoe> findByModelContaining(String model);
    List<Shoe> findByCompanyId(Long companyId);
}
