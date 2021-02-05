package shop.chobitok.modnyi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.*;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderedSpecification implements Specification<Ordered> {

    private String model;
    private String color;
    private Integer size;
    private String ttn;
    private String phoneOrName;
    private boolean withoutTTN;
    private LocalDateTime from;
    private LocalDateTime to;
    private Status status;
    private boolean excludeDeleted;
    private Boolean notForDeliveryFile;
    private String phone;
    private String isNotTtn;
    private String userId;
    private List<Status> statuses;
    private LocalDateTime dateCreatedFrom;
    private LocalDateTime dateCreatedTo;
    private Long npAccountId;

    public OrderedSpecification() {
    }

    public OrderedSpecification(List<Status> statuses, LocalDateTime dateCreatedFrom, LocalDateTime dateCreatedTo, Long npAccountId) {
        this.statuses = statuses;
        this.dateCreatedFrom = dateCreatedFrom;
        this.dateCreatedTo = dateCreatedTo;
        this.npAccountId = npAccountId;
    }

    public OrderedSpecification(String phone, String isNotTtn) {
        this.phone = phone;
        this.isNotTtn = isNotTtn;
    }

    public OrderedSpecification(Status status, Boolean notForDeliveryFile) {
        this.status = status;
        this.notForDeliveryFile = notForDeliveryFile;
    }

    public OrderedSpecification(String model, String ttn, String phoneOrName, boolean withoutTTN, String userId) {
        this.model = model;
        this.ttn = ttn;
        this.phoneOrName = phoneOrName;
        this.withoutTTN = withoutTTN;
        this.userId = userId;
    }

    public OrderedSpecification(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public OrderedSpecification(LocalDateTime from, LocalDateTime to, Status status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public OrderedSpecification(LocalDateTime from, LocalDateTime to, Status status, boolean excludeDeleted) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.excludeDeleted = excludeDeleted;
    }

    @Override
    public Predicate toPredicate(Root<Ordered> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        Predicate availablePredicate = criteriaBuilder.isTrue(root.get("available"));
        predicateList.add(availablePredicate);
        criteriaQuery.distinct(true);

        if (!StringUtils.isEmpty(ttn)) {
            Predicate ttnPredicate = criteriaBuilder.like(root.get("ttn"), "%" + ttn + "%");
            predicateList.add(ttnPredicate);
        }
        if (!StringUtils.isEmpty(phoneOrName)) {
            Join<Ordered, Client> clientJoin = root.join("client");
            Predicate phonePredicate = criteriaBuilder.like(clientJoin.get("phone"), "%" + phoneOrName + "%");
            Predicate namePredicate = criteriaBuilder.like(clientJoin.get("name"), "%" + phoneOrName + "%");
            Predicate lastNamePredicate = criteriaBuilder.like(clientJoin.get("lastName"), "%" + phoneOrName + "%");
            predicateList.add(criteriaBuilder.or(phonePredicate, namePredicate, lastNamePredicate));
        }
        if (StringUtils.isEmpty(phoneOrName) && !StringUtils.isEmpty(phone)) {
            Join<Ordered, Client> clientJoin = root.join("client");
            Predicate phonePredicate = criteriaBuilder.like(clientJoin.get("phone"), "%" + phone + "%");
            predicateList.add(phonePredicate);
        }
        if (withoutTTN) {
            Predicate withoutTTN = criteriaBuilder.isTrue(root.get("withoutTTN"));
            predicateList.add(withoutTTN);
        }
        if (from != null) {
            Predicate predicateFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), from);
            predicateList.add(predicateFrom);
        }
        if (to != null) {
            Predicate predicateTo = criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), to);
            predicateList.add(predicateTo);
        }
        if (status != null) {
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), status);
            predicateList.add(statusPredicate);
        }
        if (excludeDeleted) {
            Predicate excludeDeletedPredicate = criteriaBuilder.notEqual(root.get("status"), Status.ВИДАЛЕНО);
            predicateList.add(excludeDeletedPredicate);
        }
        if (notForDeliveryFile != null) {
            Predicate notForDeliveryFilePredicate;
            if (notForDeliveryFile) {
                notForDeliveryFilePredicate = criteriaBuilder.isTrue(root.get("notForDeliveryFile"));
            } else {
                notForDeliveryFilePredicate = criteriaBuilder.isFalse(root.get("notForDeliveryFile"));
            }
            predicateList.add(notForDeliveryFilePredicate);
        }
        if (!StringUtils.isEmpty(isNotTtn)) {
            Predicate isNotTtnPredicate = criteriaBuilder.notEqual(root.get("ttn"), isNotTtn);
            predicateList.add(isNotTtnPredicate);
        }
        if (!StringUtils.isEmpty(userId)) {
            Join<Ordered, User> orderedUserJoin = root.join("user");
            Predicate userPredicate = criteriaBuilder.equal(orderedUserJoin.get("id"), userId);
            predicateList.add(userPredicate);
        }
        if (statuses != null && statuses.size() > 0) {
            List<Predicate> statusesPredicates = new ArrayList<>();
            for (Status status : statuses) {
                statusesPredicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            predicateList.add(criteriaBuilder.or(statusesPredicates.toArray(Predicate[]::new)));
        }
        if (dateCreatedFrom != null) {
            Predicate predicateFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), from);
            predicateList.add(predicateFrom);
        }
        if (dateCreatedTo != null) {
            Predicate predicateTo = criteriaBuilder.lessThanOrEqualTo(root.get("dateCreated"), to);
            predicateList.add(predicateTo);
        }
        if (npAccountId != null) {
            Predicate predicateNpAccount = criteriaBuilder.equal(root.get("npAccountId"), npAccountId);
            predicateList.add(predicateNpAccount);
        }
        if (!StringUtils.isEmpty(model) || !StringUtils.isEmpty(color)) {
            Join<Ordered, Shoe> orderedShoeJoin = root.join("orderedShoes");
            if (!StringUtils.isEmpty(model)) {
                Predicate modelPredicate = criteriaBuilder.like(orderedShoeJoin.get("model"), "%" + model + "%");
                predicateList.add(modelPredicate);
            }
            if (!StringUtils.isEmpty(color)) {
                Predicate colorPredicate = criteriaBuilder.like(orderedShoeJoin.get("color"), "%" + color + "%");
                predicateList.add(colorPredicate);
            }
        }
        if (size != null) {
            Predicate sizePredicate = criteriaBuilder.equal(root.get("size"), size);
            predicateList.add(sizePredicate);
        }
        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public String getPhoneOrName() {
        return phoneOrName;
    }

    public void setPhoneOrName(String phoneOrName) {
        this.phoneOrName = phoneOrName;
    }

    public boolean isWithoutTTN() {
        return withoutTTN;
    }

    public void setWithoutTTN(boolean withoutTTN) {
        this.withoutTTN = withoutTTN;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isExcludeDeleted() {
        return excludeDeleted;
    }

    public void setExcludeDeleted(boolean excludeDeleted) {
        this.excludeDeleted = excludeDeleted;
    }

    public Boolean getNotForDeliveryFile() {
        return notForDeliveryFile;
    }

    public void setNotForDeliveryFile(Boolean notForDeliveryFile) {
        this.notForDeliveryFile = notForDeliveryFile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsNotTtn() {
        return isNotTtn;
    }

    public void setIsNotTtn(String isNotTtn) {
        this.isNotTtn = isNotTtn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public LocalDateTime getDateCreatedFrom() {
        return dateCreatedFrom;
    }

    public void setDateCreatedFrom(LocalDateTime dateCreatedFrom) {
        this.dateCreatedFrom = dateCreatedFrom;
    }

    public LocalDateTime getDateCreatedTo() {
        return dateCreatedTo;
    }

    public void setDateCreatedTo(LocalDateTime dateCreatedTo) {
        this.dateCreatedTo = dateCreatedTo;
    }

    public Long getNpAccountId() {
        return npAccountId;
    }

    public void setNpAccountId(Long npAccountId) {
        this.npAccountId = npAccountId;
    }

}
