package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.SentMail;

public interface SentMailRepository extends JpaRepository<SentMail, Long> {

    SentMail findByClientId(Long id);

}
