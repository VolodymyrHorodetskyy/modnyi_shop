package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.CompanyFinanceControl;

import java.util.List;

public interface CompanyFinanceControlRepository extends JpaRepository<CompanyFinanceControl, Long> {

    List<CompanyFinanceControl> findFirst10ByCompanyIdOrderByCreatedDateDesc(Long companyId, Pageable pageable);
}
