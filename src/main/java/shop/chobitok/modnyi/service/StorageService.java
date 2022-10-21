package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.exception.ConflictException;
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

    public StorageService(StorageRepository storageRepository, ShoeRepository shoeRepository) {
        this.storageRepository = storageRepository;
        this.shoeRepository = shoeRepository;
    }

    public StorageRecord saveOrUpdateStorageRecord(StorageRecord storageRecord) {
        return storageRepository.save(storageRecord);
    }

    public StorageRecord findFirstStorageRecord(Long shoeId, Integer size) {
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

    public List<StorageRecord> getStorageRecords(Long shoeId, Integer size) {
        StorageSpecification storageSpecification = new StorageSpecification();
        storageSpecification.setModelId(shoeId);
        storageSpecification.setSize(size);
        return storageRepository.findAll(storageSpecification,
                of(0, 20, Sort.by(DESC, "createdDate"))).getContent();
    }
}
