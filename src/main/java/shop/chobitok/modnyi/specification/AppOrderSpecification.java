package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.User;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppOrderSpecification implements Specification<AppOrder> {

    private Long id;
    private String phoneAndName;
    private String comment;
    private LocalDateTime from;
    private List<AppOrderStatus> statuses;
    private String phone;
    private Long isNotEqualId;
    private String userId;


    public AppOrderSpecification(String phone, Long isNotEqualId) {
        this.phone = phone;
        this.isNotEqualId = isNotEqualId;
    }

    public AppOrderSpecification(Long id, String phoneAndName, String comment, LocalDateTime from, List<AppOrderStatus> statuses, String userId) {
        this.id = id;
        this.phoneAndName = phoneAndName;
        this.comment = comment;
        this.from = from;
        this.statuses = statuses;
        this.userId = userId;
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
        if (!StringUtils.isEmpty(phone)) {
            Predicate phonePredicate = criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
            predicateList.add(phonePredicate);
        }
        if (isNotEqualId != null) {
            Predicate isNotEqualsId = criteriaBuilder.notEqual(root.get("id"), isNotEqualId);
            predicateList.add(isNotEqualsId);
        }
        if (!StringUtils.isEmpty(userId)) {
            Join<AppOrder, User> appOrderUserJoin = root.join("user");
            Predicate userPredicate = criteriaBuilder.equal(appOrderUserJoin.get("id"), userId);
            predicateList.add(userPredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

}
