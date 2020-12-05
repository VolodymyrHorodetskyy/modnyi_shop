package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.PayedOrdered;

import java.util.List;

@Repository
public interface PayedOrderedRepository extends JpaRepository<PayedOrdered, Long> {

    PayedOrdered findByTtn(String ttn);

    List<PayedOrdered> findByCountedFalse();

}
