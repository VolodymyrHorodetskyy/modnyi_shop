package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.SendEventsHistory;

public interface SendEventsHistoryRepository extends JpaRepository<SendEventsHistory, Long> {
}
