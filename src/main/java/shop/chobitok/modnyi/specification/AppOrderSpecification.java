package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.AppOrder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppOrderSpecification implements Specification<AppOrder> {

    private Long id;
    private String phoneAndName;
    private LocalDateTime from;

    public AppOrderSpecification(Long id, String phoneAndName, LocalDateTime from) {
        this.id = id;
        this.phoneAndName = phoneAndName;
        this.from = from;
    }

    @Override
    public Predicate toPredicate(Root<AppOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if (id != null) {
            Predicate idPredicate = criteriaBuilder.equal(root.get("id"), id);
            predicateList.add(idPredicate);
        }
        if (!StringUtils.isEmpty(phoneAndName)) {
            Predicate phonePredicate = criteriaBuilder.like(root.get("phone"), "%" + phoneAndName + "%");
            Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + phoneAndName + "%");
            predicateList.add(criteriaBuilder.or(phonePredicate, namePredicate));
        }
        if (from != null) {
            Predicate fromPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), from);
            predicateList.add(fromPredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

}
