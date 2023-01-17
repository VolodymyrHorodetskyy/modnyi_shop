package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.entity.response.ModelAndSizesResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.OrderedShoeRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.repository.StorageRepository;
import shop.chobitok.modnyi.specification.StorageSpecification;

import java.util.*;

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

    public Object getStorageRecords(Long shoeId, Integer size, String model,
                                    String color, Boolean available, boolean groupByModel) {
        StorageSpecification storageSpecification = new StorageSpecification();
        storageSpecification.setModelId(shoeId);
        storageSpecification.setSize(size);
        storageSpecification.setModelName(model);
        storageSpecification.setColor(color);
        storageSpecification.setAvailable(true);

        List<StorageRecord> storageRecords = storageRepository.findAll(storageSpecification,
                of(0, 200, Sort.by(DESC, "available", "createdDate"))).getContent();
        if (groupByModel) {
            Map<String, List<Integer>> baykaModelAndSizes = new HashMap<>();
            Map<String, List<Integer>> hutroModelAndSize = new HashMap<>();
            for (StorageRecord storageRecord : storageRecords) {
                if (storageRecord.getComment().toLowerCase().contains("хут")) {
                    addToMap(hutroModelAndSize, storageRecord);
                } else {
                    addToMap(baykaModelAndSizes, storageRecord);
                }
            }
            List<ModelAndSizesResponse> modelAndSizes = makeModelAndSizesResponse(hutroModelAndSize, "хутро");
            modelAndSizes.addAll(makeModelAndSizesResponse(baykaModelAndSizes, "байка"));
            return modelAndSizes;
        }
        return storageRecords;
    }

    private List<ModelAndSizesResponse> makeModelAndSizesResponse(Map<String, List<Integer>> map, String addition) {
        List<ModelAndSizesResponse> modelAndSizes = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            modelAndSizes.add(new ModelAndSizesResponse(entry.getKey() + " " + addition,
                    formSizes(entry.getValue())));
        }
        return modelAndSizes;
    }

    private String formSizes(List<Integer> sizes) {
        Map<Integer, Integer> sizesMap = new TreeMap<>();
        for (Integer size : sizes) {
            addToMap(sizesMap, size);
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : sizesMap.entrySet()) {
            Integer value = entry.getValue();
            result.append(entry.getKey());
            if (value != 1) {
                result.append(" - ").append(entry.getValue());
            }
            result.append(", ");
        }
        return result.toString();
    }

    private void addToMap(Map<Integer, Integer> map, Integer size) {
        Integer amount = map.get(size);
        if (amount == null) {
            amount = 0;
        }
        ++amount;
        map.put(size, amount);
    }

    private void addToMap(Map<String, List<Integer>> map, StorageRecord storageRecord) {
        List<Integer> sizes = map.get(storageRecord.getShoe().getModelAndColor());
        if (sizes == null) {
            sizes = new ArrayList<>();
        }
        sizes.add(storageRecord.getSize());
        map.put(storageRecord.getShoe().getModelAndColor(), sizes);
    }

    public List<StorageRecord> getStorageRecords(Long orderedShoeId) {
        OrderedShoe orderedShoe = orderedShoeRepository.findById(orderedShoeId).orElseThrow(
                () -> new ConflictException("Ordered shoe not found"));
        return (List<StorageRecord>) getStorageRecords(orderedShoe.getShoe().getId(), orderedShoe.getSize(),
                null, null, true, false);
    }

    public StorageRecord makeUnavailable(Long id) {
        StorageRecord storageRecord = storageRepository.findById(id)
                .orElseThrow(() -> new ConflictException("Запис не знайдено"));
        storageRecord.setAvailable(false);
        return storageRepository.save(storageRecord);
    }
}
