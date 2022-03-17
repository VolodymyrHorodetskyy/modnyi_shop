package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.*;
import shop.chobitok.modnyi.entity.response.ShoeWithPrice;
import shop.chobitok.modnyi.service.ShoeService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/shoe")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class ShoeController {

    private final ShoeService shoeService;

    public ShoeController(ShoeService shoeService) {
        this.shoeService = shoeService;
    }

    @GetMapping
    public List<ShoeWithPrice> getAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String modelAndColor) {
        return shoeService.getAllShoeWithPrice(page, size, modelAndColor);
    }

    @PostMapping
    public Shoe createShoe(@RequestBody CreateShoeRequest createShoeRequest) {
        return shoeService.createShoe(createShoeRequest);
    }

    @PatchMapping
    public Shoe updateShoe(@RequestBody UpdateShoeRequest updateShoeRequest) {
        return shoeService.updateShoe(updateShoeRequest);
    }

    @PatchMapping("/addPattern")
    public Shoe addPattern(@RequestBody AddOrRemovePatternRequest request) {
        return shoeService.addPattern(request);
    }

    @PatchMapping("/removePattern")
    public boolean deletePattern(@RequestBody AddOrRemovePatternRequest request) {
        return shoeService.removePattern(request);
    }

    @GetMapping("/getShoePrice")
    public Double getShoePrice(@RequestParam Long[] shoeIds, @RequestParam Long discountId) {
        return shoeService.getShoePrice(shoeIds, discountId);
    }

    @PatchMapping("/addShoeToOrder")
    public Ordered addShoeToOrder(@RequestBody AddShoeToOrderRequest request) {
        return shoeService.addShoeToOrder(request);
    }

    @PatchMapping("/removeShoeFromOrder")
    public Ordered removeShoeFromOrder(@RequestBody RemoveShoeFromOrderRequest request) {
        return shoeService.removeShoeFromOrder(request);
    }

}
