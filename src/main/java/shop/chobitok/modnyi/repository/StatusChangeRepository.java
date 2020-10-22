package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.StatusChangeRecord;

@Repository
public interface StatusChangeRepository extends JpaRepository<StatusChangeRecord, Long> {
}
