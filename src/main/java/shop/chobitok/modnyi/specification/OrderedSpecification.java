package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.Ordered;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderedSpecification implements Specification<Ordered> {

    private String model;
    private String ttn;
    private String phone;
    private boolean withoutTTN;
    private LocalDateTime from;
    private LocalDateTime to;

    public OrderedSpecification(String model, String ttn, String phone, boolean withoutTTN) {
        this.model = model;
        this.ttn = ttn;
        this.phone = phone;
        this.withoutTTN = withoutTTN;
    }

    public OrderedSpecification(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Predicate toPredicate(Root<Ordered> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        Predicate availablePredicate = criteriaBuilder.isTrue(root.get("available"));
        predicateList.add(availablePredicate);

        if (!StringUtils.isEmpty(ttn)) {
            Predicate ttnPredicate = criteriaBuilder.like(root.get("ttn"), "%" + ttn + "%");
            predicateList.add(ttnPredicate);
        }
        if (!StringUtils.isEmpty(phone)) {
            Join<Ordered, Client> clientJoin = root.join("client");
            Predicate phonePredicate = criteriaBuilder.like(clientJoin.get("phone"), "%" + phone + "%");
            predicateList.add(phonePredicate);
        }
        if (withoutTTN) {
            Predicate withoutTTN = criteriaBuilder.isTrue(root.get("withoutTTN"));
            predicateList.add(withoutTTN);
        }
        if (from != null) {
            Predicate predicateFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), from);
            predicateList.add(predicateFrom);
        }
        if(to != null){
            Predicate predicateTo = criteriaBuilder.lessThanOrEqualTo(root.get("dateCreated"), to);
            predicateList.add(predicateTo);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

}
