package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

public class StorageSpecification implements Specification<StorageRecord> {

    private Long modelId;
    private Integer size;

    @Override
    public Predicate toPredicate(Root<StorageRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate availablePredicate = criteriaBuilder.isTrue(root.get("available"));
        predicates.add(availablePredicate);
        Join<StorageRecord, Shoe> storageRecordShoeJoin = root.join("shoe");
        if (!isEmpty(modelId)) {
            Predicate modelIdPredicate = criteriaBuilder.equal(
                    storageRecordShoeJoin.get("id"), modelId);
            predicates.add(modelIdPredicate);
        }
        if (!isEmpty(size)) {
            Predicate sizePredicate = criteriaBuilder.equal(
                    root.get("size"), size);
            predicates.add(sizePredicate);
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
