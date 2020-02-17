package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.service.ShoeService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/item")
public class ShoeController {

    private ShoeService shoeService;

    public ShoeController(ShoeService shoeService) {
        this.shoeService = shoeService;
    }

    @GetMapping
    public List<Shoe> getAll() {
        return shoeService.getAll();
    }

}
