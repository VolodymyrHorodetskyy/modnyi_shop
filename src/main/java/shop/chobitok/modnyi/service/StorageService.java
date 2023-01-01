package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.OrderedShoeRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.StorageRepository;
import shop.chobitok.modnyi.specification.StorageSpecification;

import java.util.List;

import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final ShoeRepository shoeRepository;
    private final OrderedShoeRepository orderedShoeRepository;

    public StorageService(StorageRepository storageRepository, ShoeRepository shoeRepository, OrderedShoeRepository orderedShoeRepository) {
        this.storageRepository = storageRepository;
        this.shoeRepository = shoeRepository;
        this.orderedShoeRepository = orderedShoeRepository;
    }

    public StorageRecord getById(Long storageRecordId) {
        return storageRepository.findById(storageRecordId).orElse(null);
    }

    public StorageRecord saveOrUpdateStorageRecord(StorageRecord storageRecord) {
        return storageRepository.save(storageRecord);
    }

    public StorageRecord findFirstAvailableStorageRecord(Long shoeId, Integer size) {
        return storageRepository.findFirstByShoeIdAndSizeAndAvailableTrue(shoeId, size);
    }

    public StorageRecord createStorageRecord(CreateStorageRequest createStorageRequest) {
        StorageRecord storageRecord = new StorageRecord();
        Shoe shoe = shoeRepository.findById(createStorageRequest.getShoeId()).orElse(null);
        if (shoe == null) {
            throw new ConflictException("Взуття не знайдено");
        }
        storageRecord.setShoe(shoe);
        storageRecord.setSize(createStorageRequest.getSize());
        storageRecord.setComment(createStorageRequest.getComment());
        return storageRepository.save(storageRecord);
    }

    public boolean checkIfInStorage(Long shoeId, Integer size) {
        if (shoeId != null && size != null) {
            return storageRepository.findBySizeAndShoeIdAndAvailableTrue(size, shoeId).size() > 0;
        }
        return false;
    }

    public List<StorageRecord> getStorageRecords(Long shoeId, Integer size, String model,
                                                 String color, Boolean available) {
        StorageSpecification storageSpecification = new StorageSpecification();
        storageSpecification.setModelId(shoeId);
        storageSpecification.setSize(size);
        storageSpecification.setModelName(model);
        storageSpecification.setColor(color);
        storageSpecification.setAvailable(available);
        return storageRepository.findAll(storageSpecification,
                of(0, 200, Sort.by(DESC, "available","createdDate"))).getContent();
    }

    public List<StorageRecord> getStorageRecords(Long orderedShoeId) {
        OrderedShoe orderedShoe = orderedShoeRepository.findById(orderedShoeId).orElseThrow(
                () -> new ConflictException("Ordered shoe not found"));
        return getStorageRecords(orderedShoe.getShoe().getId(), orderedShoe.getSize(),
                null, null, true);
    }
}
