package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.TelegramSubscribers;

import java.util.List;

public interface TelegramSubscribersRepository extends JpaRepository<TelegramSubscribers, Long> {
    List<TelegramSubscribers> findAllByAvailableTrue();
}
