package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CanceledOrderReasonSpecification implements Specification<CanceledOrderReason> {

    private LocalDateTime from;
    private boolean statusNotReceived;
    private CancelReason cancelReason;
    private String ttn;
    private String phoneOrName;
    private Boolean manual;
    private Boolean withoutReason;
    private boolean hasReturnTtn;
    private String userId;

    public CanceledOrderReasonSpecification(LocalDateTime from, boolean statusNotReceived, String ttn, String phoneOrName, Boolean manual, Boolean withoutReason, String userId) {
        this.from = from;
        this.statusNotReceived = statusNotReceived;
        this.ttn = ttn;
        this.phoneOrName = phoneOrName;
        this.manual = manual;
        this.withoutReason = withoutReason;
        this.userId = userId;
    }

    public CanceledOrderReasonSpecification(LocalDateTime from, boolean statusNotReceived, CancelReason cancelReason) {
        this.from = from;
        this.statusNotReceived = statusNotReceived;
        this.cancelReason = cancelReason;
    }

    public CanceledOrderReasonSpecification(LocalDateTime from, boolean statusNotReceived) {
        this.from = from;
        this.statusNotReceived = statusNotReceived;
    }

    public CanceledOrderReasonSpecification(boolean statusNotReceived, boolean hasReturnTtn) {
        this.statusNotReceived = statusNotReceived;
        this.hasReturnTtn = hasReturnTtn;
    }

    @Override
    public Predicate toPredicate(Root<CanceledOrderReason> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Join<CanceledOrderReason, Ordered> orderedJoin = root.join("ordered");
        if (from != null) {
            Predicate predicateFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), from);
            predicates.add(predicateFrom);
        }
        if (statusNotReceived) {
            Predicate predicateNotReceived = criteriaBuilder.notEqual(root.get("status"), Status.ОТРИМАНО);
            Predicate isStatusNullPredicate = criteriaBuilder.isNull(root.get("status"));
            predicates.add(criteriaBuilder.or(predicateNotReceived, isStatusNullPredicate));
        }
        if (cancelReason != null) {
            Predicate predicateReason = criteriaBuilder.equal(root.get("reason"), cancelReason);
            predicates.add(predicateReason);
        }
        if (!StringUtils.isEmpty(ttn)) {
            Predicate predicateTtn = criteriaBuilder.like(orderedJoin.get("ttn"), "%" + ttn + "%");
            Predicate predicateReturnTtn = criteriaBuilder.like(root.get("returnTtn"), "%" + ttn + "%");
            Predicate predicateNewTtn = criteriaBuilder.like(root.get("newTtn"), "%" + ttn + "%");
            predicates.add(criteriaBuilder.or(predicateTtn, predicateReturnTtn, predicateNewTtn));
        }
        if (!StringUtils.isEmpty(phoneOrName)) {
            Join<Ordered, Client> orderedClientJoin = orderedJoin.join("client");
            Predicate predicateName = criteriaBuilder.like(orderedClientJoin.get("name"), "%" + phoneOrName + "%");
            Predicate predicateLastName = criteriaBuilder.like(orderedClientJoin.get("lastName"), "%" + phoneOrName + "%");
            Predicate predicatePhone = criteriaBuilder.like(orderedClientJoin.get("phone"), "%" + phoneOrName + "%");
            predicates.add(criteriaBuilder.or(predicateName, predicateLastName, predicatePhone));
        }
        if (manual != null && manual == true) {
            Predicate manualPredicate = criteriaBuilder.isTrue(root.get("manual"));
            predicates.add(manualPredicate);
        }
        if (withoutReason != null && withoutReason == true) {
            Predicate withoutReasonPredicate = criteriaBuilder.equal(root.get("reason"), CancelReason.НЕ_ВИЗНАЧЕНО);
            predicates.add(withoutReasonPredicate);
        }
        if (hasReturnTtn) {
            Predicate notNullTtnPredicate = criteriaBuilder.isNotNull(root.get("returnTtn"));
            Predicate isNotEmpty = criteriaBuilder.notEqual(root.get("returnTtn"), "");
            predicates.add(criteriaBuilder.and(notNullTtnPredicate, isNotEmpty));
        }
        if (!StringUtils.isEmpty(userId)) {
            Join<Ordered, User> userJoin = orderedJoin.join("user");
            Predicate userPredicate = criteriaBuilder.equal(userJoin.get("id"), userId);
            predicates.add(userPredicate);
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}
