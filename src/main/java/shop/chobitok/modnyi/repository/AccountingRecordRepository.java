package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AccountingRecord;

public interface AccountingRecordRepository extends JpaRepository<AccountingRecord, Long> {

    public AccountingRecord findFirstOrderByCreatedDateDesc();

}
