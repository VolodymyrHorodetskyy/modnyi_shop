package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Marking;


@Repository
public interface MarkingRepository extends JpaRepository<Marking, Long> {

    Marking findByOrderedId(Long id);

}
