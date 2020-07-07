package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTtnAndMessageType(String ttn, MessageType type);
    Page<Notification> findByRead1(boolean read, Pageable pageable);

    Integer countByRead1(boolean read);

}
