package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

public class StorageSpecification implements Specification<StorageRecord> {

    private Long modelId;
    private Integer size;
    private Boolean available;
    private String modelName;
    private String color;
    private Join<StorageRecord, Shoe> storageRecordShoeJoin = null;

    @Override
    public Predicate toPredicate(Root<StorageRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (!isEmpty(modelId)) {
            Predicate modelIdPredicate = criteriaBuilder.equal(
                    getStorageRecordShoeJoin(root).get("id"), modelId);
            predicates.add(modelIdPredicate);
        }
        if (!isEmpty(size)) {
            Predicate sizePredicate = criteriaBuilder.equal(
                    root.get("size"), size);
            predicates.add(sizePredicate);
        }
        if (available != null) {
            Predicate availablePredicate;
            if (available) {
                availablePredicate = criteriaBuilder.isTrue(root.get("available"));
            } else {
                availablePredicate = criteriaBuilder.isFalse(root.get("available"));
            }
            predicates.add(availablePredicate);
        }
        if (!isEmpty(modelName)) {
            Predicate modelNamePredicate = criteriaBuilder.like(getStorageRecordShoeJoin(root).get("model"),
                    "%" + modelName + "%");
            predicates.add(modelNamePredicate);
        }
        if (!isEmpty(color)) {
            Predicate colorPredicate = criteriaBuilder.like(getStorageRecordShoeJoin(root).get("color"),
                    "%" + color + "%");
            predicates.add(colorPredicate);
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Join getStorageRecordShoeJoin(Root<StorageRecord> root) {
        if (storageRecordShoeJoin == null) {
            storageRecordShoeJoin = root.join("shoe");
        }
        return storageRecordShoeJoin;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
