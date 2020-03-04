package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import shop.chobitok.modnyi.entity.StorageRecord;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class StorageSpecification implements Specification<StorageRecord> {

    private String model;
    private String color;
    private Integer size;

    public StorageSpecification(String model, String color, Integer size) {
        this.model = model;
        this.color = color;
        this.size = size;
    }

    @Override
    public Predicate toPredicate(Root<StorageRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate availablePredicate = criteriaBuilder.isTrue(root.get("available"));
        predicates.add(availablePredicate);
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}
