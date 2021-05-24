package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Client;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByPhone(String phone);

    List<Client> findByPhoneContains(String phone);

    List<Client> findByMailNotNull();

}
