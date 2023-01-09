package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.service.StorageService;

import static java.lang.System.out;

@RestController
@CrossOrigin
@RequestMapping("/helper")
public class HelperController {

    private StorageService storageService;

    @GetMapping("getAvailableFromStorage/{shoeId}")
    public String getAvailableSizesFromStorage(@PathVariable Long shoeId) {
        StringBuilder stringBuilder = new StringBuilder();
        storageService.getStorageRecords(shoeId, null, null, null, true)
                .forEach(storageRecord -> {
                    stringBuilder.append(storageRecord.getShoe().getModelAndColor() + " " + storageRecord.getSize() + " " + storageRecord.getComment());
                });
        return stringBuilder.toString();
    }
}
