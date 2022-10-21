package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.StorageCoincidence;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.repository.StorageCoincidenceRepository;

@Service
public class StorageCoincidenceService {

    private final StorageService storageService;
    private final StorageCoincidenceRepository storageCoincidenceRepository;
    private final OrderedShoeService orderedShoeService;

    public StorageCoincidenceService(StorageService storageService, StorageCoincidenceRepository storageCoincidenceRepository, OrderedShoeService orderedShoeService) {
        this.storageService = storageService;
        this.storageCoincidenceRepository = storageCoincidenceRepository;
        this.orderedShoeService = orderedShoeService;
    }

    @Transactional
    public StorageCoincidence tryToFind(OrderedShoe orderedShoe) {
        StorageRecord storageRecord = storageService.findFirstStorageRecord(orderedShoe.getShoe().getId(), orderedShoe.getSize());
        StorageCoincidence storageCoincidence = null;
        if (storageRecord != null) {
            storageRecord.setAvailable(false);
            storageCoincidence = new StorageCoincidence(orderedShoe, storageRecord);
            orderedShoe.setUsedInCoincidence(true);
            orderedShoe.setShouldNotBePayed(true);
            storageService.saveOrUpdateStorageRecord(storageRecord);
            storageCoincidenceRepository.save(storageCoincidence);
            orderedShoeService.saveOrUpdateOrderedShoe(orderedShoe);
        }
        return storageCoincidence;
    }
}
