package shop.chobitok.modnyi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.entity.dto.BulkPriceUpdateDto;
import shop.chobitok.modnyi.entity.request.CreateShoePriceRequest;
import shop.chobitok.modnyi.service.ShoePriceService;

@RestController
@CrossOrigin
@RequestMapping("/shoePrice")
@PreAuthorize("hasAuthority('ADMIN')")
public class ShoePriceController {

    private final ShoePriceService shoePriceService;

    public ShoePriceController(ShoePriceService shoePriceService) {
        this.shoePriceService = shoePriceService;
    }

    @PostMapping
    public ShoePrice shoePrice(@RequestBody CreateShoePriceRequest createShoePriceRequest) {
        return shoePriceService.setNewPrice(createShoePriceRequest);
    }

    @PostMapping("/update-prices")
    public ResponseEntity<Void> updateShoePrices(@RequestBody BulkPriceUpdateDto request) {
        shoePriceService.updateShoePrices(request.getShoeIds(), request.getPrice(), request.getCost(), request.getFromDate());
        return ResponseEntity.ok().build();
    }
}
