package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.CancelReason;
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
                                        @RequestParam(required = false) String phoneOrName, @RequestParam(required = false) String model, @RequestParam(required = false) boolean withoutTTN,
                                        @RequestParam(required = false) String orderBy, @RequestParam(required = false) String userId) {
        return orderService.getAll(page, size, ttn, phoneOrName, model, withoutTTN, orderBy, userId);
    }

    @GetMapping("/{id}")
    public Ordered getOrdered(@PathVariable Long id) {
        return orderService.getById(id);
    }


    @PostMapping
    public Ordered createOrdered(@RequestBody @Valid UpdateOrderRequest updateOrderRequest) {
        return orderService.updateOrder(null, updateOrderRequest);
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
        return orderService.updateOrdersByNovaPosta();
    }

    @PatchMapping("/updateStatusesWithoutResponse")
    public void updateStatusesWithoutResponse() {
        orderService.updateOrdersByNovaPosta();
    }

    @GetMapping("/getCanceled")
    public List<Ordered> getCanceledOrders(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.getCanceled(updateStatuses);
    }

    @GetMapping("/returnCargo")
    public StringResponse returnCargo(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.returnAllCanceled(updateStatuses);
    }

    @PatchMapping("/makeAllPayed")
    public StringResponse makeAllPayed() {
        return orderService.makeAllPayed();
    }
}
