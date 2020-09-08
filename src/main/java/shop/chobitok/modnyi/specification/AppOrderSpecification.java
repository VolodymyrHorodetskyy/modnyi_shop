package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;

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
    private String comment;
    private LocalDateTime from;
    private List<AppOrderStatus> statuses;

    public AppOrderSpecification(Long id, String phoneAndName, String comment, LocalDateTime from, List<AppOrderStatus> statuses) {
        this.id = id;
        this.phoneAndName = phoneAndName;
        this.comment = comment;
        this.from = from;
        this.statuses = statuses;
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
            Predicate fromPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedDate"), from);
            predicateList.add(fromPredicate);
        }
        if (statuses != null && statuses.size() > 0) {
            predicateList.add(root.get("status").in(statuses));
        }
        if (!StringUtils.isEmpty(comment)) {
            Predicate commentPredicate = criteriaBuilder.like(root.get("comment"), "%" + comment + "%");
            predicateList.add(commentPredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

}
