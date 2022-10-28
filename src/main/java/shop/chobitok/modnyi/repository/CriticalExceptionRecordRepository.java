package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.CriticalExceptionRecord;

public interface CriticalExceptionRecordRepository extends JpaRepository<CriticalExceptionRecord, Long> {
}
