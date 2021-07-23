package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.UserLoggedIn;

import java.time.LocalDateTime;
import java.util.List;

public interface UserLoggedInRepository extends JpaRepository<UserLoggedIn, Long> {

    UserLoggedIn findOneByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqualAndUserId(LocalDateTime from,
                                                                                          LocalDateTime to,
                                                                                          Long id);
    List<UserLoggedIn> findAllByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(LocalDateTime from,
                                                                                       LocalDateTime to);
    List<UserLoggedIn> findAllByActiveTrue();

}
