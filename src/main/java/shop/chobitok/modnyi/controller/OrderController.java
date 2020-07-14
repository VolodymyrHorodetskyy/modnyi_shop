package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.CancelReason;
import shop.chobitok.modnyi.entity.CanceledOrderReason;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.dto.StatusDto;
import shop.chobitok.modnyi.entity.request.*;
import shop.chobitok.modnyi.entity.response.GetAllOrderedResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.UtilService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;
    private NovaPostaService novaPostaService;
    private UtilService utilService;


    public OrderController(OrderService orderService, NovaPostaService novaPostaService, UtilService utilService) {
        this.orderService = orderService;
        this.novaPostaService = novaPostaService;
        this.utilService = utilService;
    }

    @GetMapping
    public GetAllOrderedResponse getAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) String ttn,
                                        @RequestParam(required = false) String phone, @RequestParam(required = false) String model, @RequestParam(required = false) boolean withoutTTN,
                                        @RequestParam(required = false) String orderBy) {
        return orderService.getAll(page, size, ttn, phone, model, withoutTTN, orderBy);
    }

    @PostMapping("/fromNP")
    public Ordered createOrderFromNP(@RequestBody FromNPToOrderRequest fromNPToOrderRequest) {
        return novaPostaService.createOrderFromNP(fromNPToOrderRequest);
    }

    @PostMapping
    public Ordered createOrdered(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    @PatchMapping("/{id}")
    public Ordered updateOrdered(@RequestBody @Valid UpdateOrderRequest updateOrderRequest, @PathVariable Long id) {
        return orderService.updateOrder(id, updateOrderRequest);
    }

    @GetMapping("/getStatuses")
    public List<StatusDto> getStatuses() {
        return utilService.getStatuses();
    }

    @GetMapping("/getReasons")
    public String[] getReasons() {
        return Arrays.stream(CancelReason.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    @PostMapping("/importOrdersByTTNsString")
    public StringResponse importOrdersFromTTNList(@RequestBody ImportOrdersFromStringRequest request) {
        return orderService.importOrdersByTTNString(request);
    }

    @PatchMapping("/updateStatuses")
    public String updateStatuses() {
        return orderService.updateOrderStatusesNovaPosta();
    }

    @GetMapping("/getCanceled")
    public List<Ordered> getCanceledOrders(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.getCanceled(updateStatuses);
    }

    @GetMapping("/returnCargo")
    public StringResponse returnCargo(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.returnAllCanceled(updateStatuses);
    }

    @PatchMapping("/cancelOrder")
    public Ordered cancelOrdered(@RequestBody CancelOrderRequest request) {
        return orderService.cancelOrder(request);
    }

    @GetMapping("/getCanceledOrder")
    public CanceledOrderReason getCancelOrder(@RequestParam Long id) {
        return orderService.getCanceledOrderReason(id);
    }

    @PutMapping("/addShoeToOrder")
    public Ordered addShoeToOrder(@RequestBody AddShoeToOrderRequest addShoeToOrderRequest) {
        return orderService.addShoeToOrder(addShoeToOrderRequest);
    }

    @PatchMapping("/makeAllPayed")
    public boolean makeAllPayed(){
        return orderService.makeAllPayed();
    }

    @PostMapping("/webhook")
    public void webhook(@RequestBody String s){
        System.out.println(s);
    }


}
