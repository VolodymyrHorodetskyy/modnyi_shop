package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.StorageCoincidence;
import shop.chobitok.modnyi.entity.dto.StorageCoincidenceDto;
import shop.chobitok.modnyi.entity.request.AddStorageCoincidenceRequest;
import shop.chobitok.modnyi.service.StorageCoincidenceService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/storageCoinceidence")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class StorageCoincidenceController {

    private final StorageCoincidenceService storageCoincidenceService;

    public StorageCoincidenceController(StorageCoincidenceService storageCoincidenceService) {
        this.storageCoincidenceService = storageCoincidenceService;
    }

    @GetMapping
    public List<StorageCoincidenceDto> getAll() {
        return storageCoincidenceService.getAllStorageCoincidences();
    }

    @PostMapping
    public StorageCoincidence save(@RequestBody AddStorageCoincidenceRequest addStorageCoincidenceRequest) {
        return storageCoincidenceService.save(addStorageCoincidenceRequest);
    }

    @PatchMapping("/approveOrDisapprove")
    public StorageCoincidence approveOrDisapprove(@RequestParam Long id,
                                                  @RequestParam boolean action) {
        return storageCoincidenceService.approveOrDisapprove(id, action);
    }
}
