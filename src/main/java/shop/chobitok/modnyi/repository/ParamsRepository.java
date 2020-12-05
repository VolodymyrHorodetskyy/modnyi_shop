package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.chobitok.modnyi.entity.Params;

@Repository
public interface ParamsRepository extends JpaRepository<Params, Long> {

    Params findByKey(String key);

}
