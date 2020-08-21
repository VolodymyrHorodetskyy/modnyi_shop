package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.entity.request.CreateShoePriceRequest;
import shop.chobitok.modnyi.service.ShoePriceService;

@RestController
@CrossOrigin
@RequestMapping("/shoePrice")
public class ShoePriceController {

    private ShoePriceService shoePriceService;

    public ShoePriceController(ShoePriceService shoePriceService) {
        this.shoePriceService = shoePriceService;
    }

    @PostMapping
    public ShoePrice shoePrice(@RequestBody CreateShoePriceRequest createShoePriceRequest) {
        return shoePriceService.setNewPrice(createShoePriceRequest);
    }

}
