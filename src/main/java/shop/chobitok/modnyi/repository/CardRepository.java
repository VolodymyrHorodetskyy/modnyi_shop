package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardMask(String name);

}
