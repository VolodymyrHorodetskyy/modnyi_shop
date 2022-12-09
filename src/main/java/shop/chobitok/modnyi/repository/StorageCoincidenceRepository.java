package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.StorageCoincidence;

import java.util.List;

public interface StorageCoincidenceRepository extends JpaRepository<StorageCoincidence, Long> {

    List<StorageCoincidence> findAllByOrderByCreatedDateDesc();
    List<StorageCoincidence> findAllByApprovedIsNull();
}
