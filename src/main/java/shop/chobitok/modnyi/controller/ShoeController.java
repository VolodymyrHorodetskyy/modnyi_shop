package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Shoe;
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

    @GetMapping("/fromTildaCSV")
    public List<Shoe> fromTildaCSV(@RequestParam String path) {
        return shoeService.fromTildaCSV(path);
    }

}
