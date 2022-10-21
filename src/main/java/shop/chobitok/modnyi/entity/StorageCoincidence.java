package shop.chobitok.modnyi.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class StorageCoincidence extends Audit {

    @OneToOne
    private OrderedShoe orderedShoe;
    @OneToOne
    private StorageRecord storageRecord;
    private boolean resolved;

    public StorageCoincidence(OrderedShoe orderedShoe, StorageRecord storageRecord) {
        this.orderedShoe = orderedShoe;
        this.storageRecord = storageRecord;
    }

    public OrderedShoe getOrderedShoe() {
        return orderedShoe;
    }

    public void setOrderedShoe(OrderedShoe orderedShoe) {
        this.orderedShoe = orderedShoe;
    }

    public StorageRecord getStorageRecord() {
        return storageRecord;
    }

    public void setStorageRecord(StorageRecord storageRecord) {
        this.storageRecord = storageRecord;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
