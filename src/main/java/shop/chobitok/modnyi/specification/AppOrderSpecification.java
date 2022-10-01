package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderStatus;
import shop.chobitok.modnyi.entity.User;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

public class AppOrderSpecification implements Specification<AppOrder> {

    private Long id;
    private String phoneAndName;
    private String comment;
    private LocalDateTime fromLastModifiedDate;
    private LocalDateTime fromCreatedDate;
    private LocalDateTime toCreatedDate;
    private List<AppOrderStatus> statuses;
    private String phone;
    private Long isNotEqualId;
    private String userId;
    private boolean previousStatusNull;
    private String infoLike;
    private Boolean dataValid;

    public AppOrderSpecification() {
    }

    public AppOrderSpecification(String phone, Long isNotEqualId) {
        this.phone = phone;
        this.isNotEqualId = isNotEqualId;
    }

    public AppOrderSpecification(Long id, String phoneAndName, String comment, LocalDateTime fromLastModifiedDate, List<AppOrderStatus> statuses, String userId) {
        this.id = id;
        this.phoneAndName = phoneAndName;
        this.comment = comment;
        this.fromLastModifiedDate = fromLastModifiedDate;
        this.statuses = statuses;
        this.userId = userId;
    }

    public AppOrderSpecification(String phoneAndName, String comment, List<AppOrderStatus> statuses, boolean previousStatusNull) {
        this.phoneAndName = phoneAndName;
        this.comment = comment;
        this.statuses = statuses;
        this.previousStatusNull = previousStatusNull;
    }

    @Override
    public Predicate toPredicate(Root<AppOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if (id != null) {
            Predicate idPredicate = criteriaBuilder.equal(root.get("id"), id);
            predicateList.add(idPredicate);
        }
        if (!isEmpty(phoneAndName)) {
            Predicate phonePredicate = criteriaBuilder.like(root.get("phone"), "%" + phoneAndName + "%");
            Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + phoneAndName + "%");
            predicateList.add(criteriaBuilder.or(phonePredicate, namePredicate));
        }
        if (fromLastModifiedDate != null) {
            Predicate fromPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedDate"), fromLastModifiedDate);
            predicateList.add(fromPredicate);
        }
        if (statuses != null && statuses.size() > 0) {
            predicateList.add(root.get("status").in(statuses));
        }
        if (!isEmpty(comment)) {
            Predicate commentPredicate = criteriaBuilder.like(root.get("comment"), "%" + comment + "%");
            predicateList.add(commentPredicate);
        }
        if (!isEmpty(phone)) {
            Predicate phonePredicate = criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
            predicateList.add(phonePredicate);
        }
        if (isNotEqualId != null) {
            Predicate isNotEqualsId = criteriaBuilder.notEqual(root.get("id"), isNotEqualId);
            predicateList.add(isNotEqualsId);
        }
        if (!isEmpty(userId)) {
            Join<AppOrder, User> appOrderUserJoin = root.join("user");
            Predicate userPredicate = criteriaBuilder.equal(appOrderUserJoin.get("id"), userId);
            predicateList.add(userPredicate);
        }
        if (previousStatusNull) {
            Predicate previousStatusIsNullPredicate = criteriaBuilder.isNull(root.get("previousStatus"));
            predicateList.add(previousStatusIsNullPredicate);
        }
        if (fromCreatedDate != null) {
            Predicate fromCreatedDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), fromCreatedDate);
            predicateList.add(fromCreatedDatePredicate);
        }
        if (toCreatedDate != null) {
            Predicate toCreatedDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), toCreatedDate);
            predicateList.add(toCreatedDatePredicate);
        }
        if (!isEmpty(infoLike)) {
            Predicate infoLikePredicate = criteriaBuilder.like(root.get("info"), "%" + infoLike + "%");
            predicateList.add(infoLikePredicate);
        }
        if (dataValid != null) {
            Predicate dataValidPredicate;
            if (dataValid) {
                dataValidPredicate = criteriaBuilder.isTrue(root.get("dataValid"));
            } else {
                dataValidPredicate = criteriaBuilder.isFalse(root.get("dataValid"));
            }
            predicateList.add(dataValidPredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhoneAndName(String phoneAndName) {
        this.phoneAndName = phoneAndName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setFromLastModifiedDate(LocalDateTime fromLastModifiedDate) {
        this.fromLastModifiedDate = fromLastModifiedDate;
    }

    public void setFromCreatedDate(LocalDateTime fromCreatedDate) {
        this.fromCreatedDate = fromCreatedDate;
    }

    public void setStatuses(List<AppOrderStatus> statuses) {
        this.statuses = statuses;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIsNotEqualId(Long isNotEqualId) {
        this.isNotEqualId = isNotEqualId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPreviousStatusNull(boolean previousStatusNull) {
        this.previousStatusNull = previousStatusNull;
    }

    public void setToCreatedDate(LocalDateTime toCreatedDate) {
        this.toCreatedDate = toCreatedDate;
    }

    public String getInfoLike() {
        return infoLike;
    }

    public void setInfoLike(String infoLike) {
        this.infoLike = infoLike;
    }

    public Boolean getDataValid() {
        return dataValid;
    }

    public void setDataValid(Boolean dataValid) {
        this.dataValid = dataValid;
    }
}
