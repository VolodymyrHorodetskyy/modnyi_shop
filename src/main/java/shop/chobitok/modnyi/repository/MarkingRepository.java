package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Marking;
import shop.chobitok.modnyi.entity.Status;

import java.util.List;


@Repository
public interface MarkingRepository extends JpaRepository<Marking, Long> {

    Marking findByOrderedId(Long id);

    List<Marking> findByOrderedStatusAndPrintedTrue(Status status);

}
