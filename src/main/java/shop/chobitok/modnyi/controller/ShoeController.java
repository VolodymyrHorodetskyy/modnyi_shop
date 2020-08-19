package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.AddOrRemovePatternRequest;
import shop.chobitok.modnyi.entity.request.CreateShoeRequest;
import shop.chobitok.modnyi.entity.request.UpdateShoeRequest;
import shop.chobitok.modnyi.service.ShoeService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/shoe")
public class ShoeController {

    private ShoeService shoeService;

    public ShoeController(ShoeService shoeService) {
        this.shoeService = shoeService;
    }

    @GetMapping
    public List<Shoe> getAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String model) {
        return shoeService.getAll(page, size, model);
    }

    @PostMapping
    public Shoe createShoe(@RequestBody CreateShoeRequest createShoeRequest) {
        return shoeService.createShoe(createShoeRequest);
    }

    @PatchMapping
    public Shoe updateShoe(@RequestBody UpdateShoeRequest updateShoeRequest) {
        return shoeService.updateShoe(updateShoeRequest);
    }

    @DeleteMapping
    public boolean deleteShoe(@RequestParam Long id) {
        return shoeService.removeShoe(id);
    }

    @PatchMapping("/addPattern")
    public Shoe addPattern(@RequestBody AddOrRemovePatternRequest request) {
        return shoeService.addPattern(request);
    }

    @PatchMapping("/removePattern")
    public boolean deletePattern(@RequestBody AddOrRemovePatternRequest request) {
        return shoeService.removePattern(request);
    }



}
