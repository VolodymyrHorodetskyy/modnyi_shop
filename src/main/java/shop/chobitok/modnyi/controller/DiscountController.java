package shop.chobitok.modnyi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.entity.Discount;
import shop.chobitok.modnyi.service.DiscountService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/discount")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    public List<Discount> getAll() {
        return discountService.getAll();
    }

}
