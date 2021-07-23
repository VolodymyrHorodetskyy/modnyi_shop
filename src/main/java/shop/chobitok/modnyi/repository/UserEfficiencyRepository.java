package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.UserEfficiency;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEfficiencyRepository extends JpaRepository<UserEfficiency, Long> {

    List<UserEfficiency> findByCreatedDateGreaterThanEqualAndUserId(LocalDateTime from, Long userId);

}
