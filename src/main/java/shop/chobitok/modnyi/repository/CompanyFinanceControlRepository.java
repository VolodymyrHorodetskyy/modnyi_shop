package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.CompanyFinanceControl;

public interface CompanyFinanceControlRepository extends JpaRepository<CompanyFinanceControl, Long> {

    CompanyFinanceControl findOneByCompanyIdOrderByCreatedDateDesc(Long companyId);
}
