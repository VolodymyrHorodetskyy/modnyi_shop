package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.NpAccount;

@Repository
public interface NpAccountRepository extends JpaRepository<NpAccount, Long> {

}
