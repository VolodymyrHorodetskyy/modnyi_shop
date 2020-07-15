package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.AppOrder;

public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {
}
