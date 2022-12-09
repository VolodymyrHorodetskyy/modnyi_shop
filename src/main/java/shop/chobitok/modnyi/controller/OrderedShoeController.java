package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.request.UpdateOrderedShoeRequest;
import shop.chobitok.modnyi.service.OrderedShoeService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/orderedShoe")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class OrderedShoeController {

    private final OrderedShoeService orderedShoeService;

    public OrderedShoeController(OrderedShoeService orderedShoeService) {
        this.orderedShoeService = orderedShoeService;
    }

    @PatchMapping
    public OrderedShoe updateOrderedShoe(@RequestBody UpdateOrderedShoeRequest request) {
        return orderedShoeService.updateShoe(request);
    }

    @GetMapping
    public List<OrderedShoe> getOrderedShoe(@RequestParam String ttn) {
        return orderedShoeService.getOrderedShoe(ttn);
    }
}
