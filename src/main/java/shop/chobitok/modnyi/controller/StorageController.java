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

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public StorageRecord createStorage(@RequestBody CreateStorageRequest createStorageRequest) {
        return storageService.createStorageRecord(createStorageRequest);
    }

    @GetMapping
    public List<StorageRecord> getAll(@RequestParam(required = false) String model) {
        return storageService.getAll(model);
    }

    @GetMapping("/isExists")
    public boolean isAvailable(@RequestParam(required = false) Long shoeId, @RequestParam(required = false) Integer size) {
        return storageService.checkIfInStorage(shoeId, size);
    }

}
