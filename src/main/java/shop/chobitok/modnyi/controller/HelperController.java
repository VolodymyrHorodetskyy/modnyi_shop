package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.service.StorageService;

import static java.lang.System.out;

@RestController
@CrossOrigin
@RequestMapping("/helper")
public class HelperController {

    private StorageService storageService;

    @GetMapping("getAvailableFromStorage")
    public String getAvailableSizesFromStorage() {
        StringBuilder stringBuilder = new StringBuilder();
        storageService.getStorageRecords(17950l, null, null, null, true)
                .forEach(storageRecord -> {
                    stringBuilder.append(storageRecord.getShoe().getModelAndColor() + " " + storageRecord.getSize() + " " + storageRecord.getComment());
                });
        return stringBuilder.toString();
    }
}
