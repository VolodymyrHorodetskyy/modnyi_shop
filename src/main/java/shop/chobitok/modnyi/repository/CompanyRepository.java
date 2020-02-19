package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
