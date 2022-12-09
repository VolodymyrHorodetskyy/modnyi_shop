package shop.chobitok.modnyi.entity.dto;

import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.StorageCoincidence;

public class StorageCoincidenceDto extends StorageCoincidence {

    private Ordered ordered;

    public StorageCoincidenceDto(Ordered ordered, StorageCoincidence storageCoincidence) {
        this.ordered = ordered;
        setId(storageCoincidence.getId());
        setOrderedShoe(storageCoincidence.getOrderedShoe());
        setStorageRecord(storageCoincidence.getStorageRecord());
        setApproved(storageCoincidence.getApproved());
        setCreatedDate(storageCoincidence.getCreatedDate());
        setLastModifiedDate(storageCoincidence.getLastModifiedDate());
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }
}
