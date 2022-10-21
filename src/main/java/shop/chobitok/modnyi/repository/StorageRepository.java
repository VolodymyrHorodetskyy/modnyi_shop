package shop.chobitok.modnyi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.StorageRecord;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<StorageRecord, Long> {

    Page<StorageRecord> findAll(Specification specification, Pageable pageable);

    List<StorageRecord> findAll(Specification specification);

    StorageRecord findFirstByShoeIdAndSizeAndAvailableTrue(Long shoeId, Integer size);

    List<StorageRecord> findBySizeAndShoeIdAndAvailableTrue(Integer size, Long id);
}
