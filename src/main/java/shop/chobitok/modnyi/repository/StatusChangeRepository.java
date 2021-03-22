package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.StatusChangeRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatusChangeRepository extends JpaRepository<StatusChangeRecord, Long> {

    public List<StatusChangeRecord> findAllByCreatedDateGreaterThanEqualAndNewStatus(LocalDateTime dateTime, Status status);

}
