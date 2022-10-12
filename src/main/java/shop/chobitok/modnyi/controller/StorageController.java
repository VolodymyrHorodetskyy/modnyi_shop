package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.entity.request.CreateStorageRequest;
import shop.chobitok.modnyi.service.StorageService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public StorageRecord createStorage(@RequestBody CreateStorageRequest createStorageRequest) {
        return storageService.createStorageRecord(createStorageRequest);
    }

    @GetMapping("/isExists")
    public boolean isAvailable(@RequestParam(required = false) Long shoeId, @RequestParam(required = false) Integer size) {
        return storageService.checkIfInStorage(shoeId, size);
    }

    @GetMapping
    public List<StorageRecord> storageRecords(@RequestParam(required = false) Long shoeId,
                                              @RequestParam(required = false) Integer size) {
        return storageService.getStorageRecords(shoeId, size);
    }
}
