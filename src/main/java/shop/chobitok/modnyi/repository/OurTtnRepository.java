package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.OurTTN;
import shop.chobitok.modnyi.entity.Status;

import java.util.List;

@Repository
public interface OurTtnRepository extends JpaRepository<OurTTN, Long> {

    OurTTN findFirstByTtn(String ttn);

     List<OurTTN> findAllByStatusNot(Status status);

}
