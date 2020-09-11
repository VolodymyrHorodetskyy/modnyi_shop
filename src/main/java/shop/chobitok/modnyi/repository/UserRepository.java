package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
