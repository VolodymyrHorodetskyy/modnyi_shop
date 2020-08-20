package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ShoePrice shoePrice(CreateShoePriceRequest createShoePriceRequest) {
        return shoePriceService.setNewPrice(createShoePriceRequest);
    }

}
