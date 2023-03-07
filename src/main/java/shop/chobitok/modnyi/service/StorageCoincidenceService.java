package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.entity.dto.StorageCoincidenceDto;
import shop.chobitok.modnyi.entity.request.AddStorageCoincidenceRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.StorageCoincidenceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageCoincidenceService {

    private final StorageService storageService;
    private final StorageCoincidenceRepository storageCoincidenceRepository;
    private final OrderedShoeService orderedShoeService;
    private final OrderRepository orderRepository;
    private final CompanyService companyService;

    public StorageCoincidenceService(StorageService storageService, StorageCoincidenceRepository storageCoincidenceRepository, OrderedShoeService orderedShoeService, OrderRepository orderRepository, CompanyService companyService) {
        this.storageService = storageService;
        this.storageCoincidenceRepository = storageCoincidenceRepository;
        this.orderedShoeService = orderedShoeService;
        this.orderRepository = orderRepository;
        this.companyService = companyService;
    }

    @Transactional
    public StorageCoincidence tryToFind(OrderedShoe orderedShoe) {
        if (orderedShoe == null) {
            throw new ConflictException("ordered shoe cannot be null");
        }
        StorageRecord storageRecord = storageService.findFirstAvailableStorageRecord(orderedShoe.getShoe().getId(), orderedShoe.getSize());
        StorageCoincidence storageCoincidence = null;
        if (storageRecord != null) {
            storageCoincidence = new StorageCoincidence(orderedShoe, storageRecord);
            setUnavailable(orderedShoe, storageRecord);
            storageService.saveOrUpdateStorageRecord(storageRecord);
            orderedShoeService.saveOrUpdateOrderedShoe(orderedShoe);
            storageCoincidenceRepository.save(storageCoincidence);
        }
        return storageCoincidence;
    }

    @Transactional
    public StorageCoincidence approveOrDisapprove(Long storageCoincidenceRecordId, boolean action) {
        StorageCoincidence storageCoincidence = storageCoincidenceRepository.findById(storageCoincidenceRecordId).orElse(null);
        OrderedShoe orderedShoe = storageCoincidence.getOrderedShoe();
        StorageRecord storageRecord = storageCoincidence.getStorageRecord();
        if (action) {
            setUnavailable(orderedShoe, storageRecord);
        } else {
            storageRecord.setAvailable(true);
            orderedShoe.setUsedInCoincidence(false);
            orderedShoe.setShouldNotBePayed(false);
        }
        storageCoincidence.setApproved(action);
        storageService.saveOrUpdateStorageRecord(storageRecord);
        orderedShoeService.saveOrUpdateOrderedShoe(orderedShoe);
        return storageCoincidenceRepository.save(storageCoincidence);
    }

    public StorageCoincidence save(AddStorageCoincidenceRequest request) {
        StorageRecord storageRecord = storageService.getById(request.getStorageRecordId());
        OrderedShoe orderedShoe = orderedShoeService.getById(request.getOrderedShoeId());
        setUnavailable(orderedShoe, storageRecord);
        return storageCoincidenceRepository.save(new StorageCoincidence(orderedShoe, storageRecord, true));
    }

    public List<StorageCoincidenceDto> getAllStorageCoincidences() {
        return storageCoincidenceRepository.findAllByOrderByCreatedDateDesc().stream().map(
                storageCoincidence -> {
                    Ordered ordered = orderRepository.findByOrderedShoeId(storageCoincidence.getOrderedShoe().getId());
                    return new StorageCoincidenceDto(ordered, storageCoincidence);
                }
        ).collect(Collectors.toList());
    }

    private void setUnavailable(OrderedShoe orderedShoe, StorageRecord storageRecord) {
        storageRecord.setAvailable(false);
        orderedShoe.setUsedInCoincidence(true);
        Company company = companyService.getCompany(orderedShoe.getCompanyId());
        if (company.getMarkOSShouldNotBePayedIfUsedInCoincidence()) {
            orderedShoe.setShouldNotBePayed(true);
        }
    }


    public List<StorageCoincidence> findNotResolvedStorageCoincidences(){
        return storageCoincidenceRepository.findAllByApprovedIsNull();
    }
}
