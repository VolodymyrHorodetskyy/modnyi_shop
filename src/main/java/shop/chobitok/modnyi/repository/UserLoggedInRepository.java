package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.UserLoggedIn;

import java.time.LocalDateTime;

public interface UserLoggedInRepository extends JpaRepository<UserLoggedIn, Long> {

    UserLoggedIn findOneByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(LocalDateTime from,
                                                                                 LocalDateTime to);

}
