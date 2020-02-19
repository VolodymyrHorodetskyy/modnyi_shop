package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Shoe;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ShoeSpecification implements Specification<Shoe> {

    private String model;

    public ShoeSpecification(String model) {
        this.model = model;
    }

    @Override
    public Predicate toPredicate(Root<Shoe> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        Predicate availablePredicate = criteriaBuilder.isTrue(root.get("available"));
        Predicate notDeleted = criteriaBuilder.isFalse(root.get("deleted"));
        predicateList.add(availablePredicate);
        predicateList.add(notDeleted);
        if (!StringUtils.isEmpty(model)) {
            Predicate containsPredicate = criteriaBuilder.like(root.get("model"), "%" + model + "%");
            predicateList.add(containsPredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }
}
