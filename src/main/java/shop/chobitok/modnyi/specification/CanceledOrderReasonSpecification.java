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

    public CanceledOrderReasonSpecification(LocalDateTime from, boolean statusNotReceived, String ttn, String phoneOrName, Boolean manual) {
        this.from = from;
        this.statusNotReceived = statusNotReceived;
        this.ttn = ttn;
        this.phoneOrName = phoneOrName;
        this.manual = manual;
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
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}
