package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.request.CreateOrderRequest;
import shop.chobitok.modnyi.entity.request.FromNPToOrderRequest;
import shop.chobitok.modnyi.entity.request.FromTTNFileRequest;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;
    private NovaPostaService novaPostaService;

    public OrderController(OrderService orderService, NovaPostaService novaPostaService) {
        this.orderService = orderService;
        this.novaPostaService = novaPostaService;
    }

    @GetMapping
    public List<Ordered> getAll() {
        return orderService.getAll(0, 0, "", "");
    }

    @PostMapping("/fromNP")
    public Ordered createOrderFromNP(@RequestBody FromNPToOrderRequest fromNPToOrderRequest) {
        return novaPostaService.createOrderFromNP(fromNPToOrderRequest);
    }

    @PostMapping("/fromTTNFile")
    public List<Ordered> createListFromTTN(@RequestBody FromTTNFileRequest fromTTNFileRequest) {
        return novaPostaService.createFromTTNListAndSave(fromTTNFileRequest);
    }

    @PostMapping
    public Ordered createOrdered(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }


}
