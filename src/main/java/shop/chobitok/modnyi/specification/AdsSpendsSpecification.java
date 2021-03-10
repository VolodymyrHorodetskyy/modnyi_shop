package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import shop.chobitok.modnyi.entity.DaySpendRec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdsSpendsSpecification implements Specification<DaySpendRec> {

    private LocalDate from;
    private LocalDate to;

    public AdsSpendsSpecification(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Predicate toPredicate(Root<DaySpendRec> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if (from != null) {
            Predicate startFromPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("spendDate"), from);
            Predicate startToPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("spendDate"), to);
            predicateList.add(criteriaBuilder.and(startFromPredicate, startToPredicate));
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }


}
