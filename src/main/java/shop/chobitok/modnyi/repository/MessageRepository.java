package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.entity.MessageType;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTtnAndMessageType(String ttn, MessageType type);
}
