package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.StorageRepository;

import java.util.List;

@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final ShoeRepository shoeRepository;

    public StorageService(StorageRepository storageRepository, ShoeRepository shoeRepository) {
        this.storageRepository = storageRepository;
        this.shoeRepository = shoeRepository;
    }

    public StorageRecord createStorageRecord(CreateStorageRequest createStorageRequest) {
        StorageRecord storageRecord = new StorageRecord();
        Shoe shoe = shoeRepository.findById(createStorageRequest.getShoeId()).orElse(null);
        if (shoe == null) {
            throw new ConflictException("Взуття не знайдено");
        }
        storageRecord.setShoe(shoe);
        storageRecord.setSize(createStorageRequest.getSize());
        return storageRepository.save(storageRecord);
    }

    public boolean checkIfInStorage(Long shoeId, Integer size) {
        if (shoeId != null && size != null) {
            return storageRepository.findBySizeAndShoeIdAndAvailableTrue(size, shoeId).size() > 0;
        }
        return false;
    }

    public List<StorageRecord> getStorageRecords(Long shoeId, Integer size) {
        if (shoeId != null && size != null) {
            List<StorageRecord> storageRecords = storageRepository.findBySizeAndShoeIdAndAvailableTrue(size, shoeId);
            if (storageRecords.size() > 0 && storageRecords.get(0).getShoe().getCompany().getUseStorage()) {
                return storageRecords;
            }
        }
        return null;
    }
}
