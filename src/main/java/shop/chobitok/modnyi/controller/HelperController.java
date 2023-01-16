package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.StorageRecord;
import shop.chobitok.modnyi.service.StorageService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/helper")
public class HelperController {

    private StorageService storageService;

    public HelperController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("getAvailableFromStorage/{shoeId}")
    public String getAvailableSizesFromStorage(@PathVariable Long shoeId) {
        StringBuilder stringBuilder = new StringBuilder();
        List<StorageRecord> storageRecords = (List<StorageRecord>) storageService.getStorageRecords(shoeId, null, null, null, true, false);
        storageRecords
                .forEach(storageRecord -> stringBuilder.append(storageRecord.getShoe().getModelAndColor()).append(" ")
                        .append(storageRecord.getSize()).append(" ").append(storageRecord.getComment()).append("\n"));
        return stringBuilder.toString();
    }
}
