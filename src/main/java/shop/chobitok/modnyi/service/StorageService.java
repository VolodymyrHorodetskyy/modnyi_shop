package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.StorageRepository;
import shop.chobitok.modnyi.specification.StorageSpecification;

import java.util.List;

@Service
public class StorageService {

    private StorageRepository storageRepository;
    private ShoeRepository shoeRepository;

    public StorageService(StorageRepository storageRepository, ShoeRepository shoeRepository) {
        this.storageRepository = storageRepository;
        this.shoeRepository = shoeRepository;
    }

    public List<StorageRecord> getAll(String model) {
        return storageRepository.findAll(new StorageSpecification(null, null, null));
    }


    public StorageRecord createStorageRecord(CreateStorageRequest createStorageRequest) {
        StorageRecord storageRecord = new StorageRecord();
        Shoe shoe = shoeRepository.getOne(createStorageRequest.getShoe());
        if (shoe == null) {
            throw new ConflictException("Взуття не знайдено");
        }
        storageRecord.setShoe(shoe);
        storageRecord.setSize(createStorageRequest.getSize());
        storageRecord.setTtn(createStorageRequest.getTtn());

        return storageRepository.save(storageRecord);
    }

    public boolean checkIfInStorage(Long shoeId, Integer size) {
        if (shoeId != null && size != null) {
            return storageRepository.findBySizeAndShoeId(size, shoeId).size() > 0;
        }
        return false;
    }

/*    public StorageRecord setStorage(Ordered ordered) {
        if (ordered.getOrderedShoes().size() > 0) {
            List<StorageRecord> storageRecordList = storageRepository.findBySizeAndShoeId(ordered.getSize(), ordered.getOrderedShoes().get(0).getId());
            if (storageRecordList.size() > 0) {
                StorageRecord storageRecord = storageRecordList.get(0);
                storageRecord.setAvailable(false);
                return storageRepository.save(storageRecord);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }*/


}
