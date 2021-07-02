package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.History;
import shop.chobitok.modnyi.entity.HistoryType;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page findAllByType(Pageable pageable, HistoryType type);

}
