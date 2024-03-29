package shop.chobitok.modnyi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.AmountsInfoResponse;
import shop.chobitok.modnyi.entity.response.GoogleChartObject;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.google.docs.service.GoogleDocsService;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.StatisticService;
import shop.chobitok.modnyi.service.entity.StatShoe;
import shop.chobitok.modnyi.util.StringHelper;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/statistic")
public class StatisticController {

    private final StatisticService statisticService;
    private final OrderService orderService;
    private final CanceledOrderReasonService canceledOrderReasonService;

    public StatisticController(StatisticService statisticService, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService) {
        this.statisticService = statisticService;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @GetMapping("/getAllReceivedAndDeniedCount")
    public String getReceivedAndDeniedCount(@RequestParam String path) {
        return statisticService.countAllReceivedAndDenied(path);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @GetMapping("/getAllDenied")
    public String getAllDenied(@RequestParam String pathAllTTNFile, @RequestParam(required = false) boolean returned) {
        return statisticService.getAllDenied(pathAllTTNFile, returned);
    }

    @GetMapping("/needDeliveryFromDB")
    public StringResponse needDelivery(@RequestParam(required = false) boolean updateStatuses,
                                       @RequestParam Long companyId) {
        return orderService.countNeedDeliveryFromDB(updateStatuses, companyId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @GetMapping("/getIssueOrdered")
    public StringResponse getIssueOrders() {
        return statisticService.getIssueOrders();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/returned")
    public StringResponse returned(@RequestParam boolean showClientTTN,
                                   @RequestParam boolean showOnlyDelivered) {
        return canceledOrderReasonService.getReturned(false, false, showClientTTN, showOnlyDelivered, null);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/canceled")
    public StringResponse canceled(@RequestParam(required = false) boolean updateStatuses) {
        return orderService.getCanceledString(updateStatuses);
    }

    @GetMapping("/getSoldShoeRating")
    public StringResponse getSoldShoeRating(@RequestParam(required = false) String from, @RequestParam(required = false) String to,
                                            @RequestParam(required = false) Status status) {
        return StringHelper.fromSoldShoeResponse(statisticService.getOrderedShoesStats(from, to, status));
    }

    @GetMapping("/getStatisticGoogleChart")
    public GoogleChartObject getGoogleChartObject(@RequestParam(required = false) String from, @RequestParam(required = false) String to,
                                                  @RequestParam(required = false) Status status) {
        return statisticService.getShoeOrderChart(from, to, status);
    }

    @GetMapping("/getReceivedShoePercentage")
    public List<StatShoe> getReceivedShoePercentage(@RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        return statisticService.getReceivedPercentage(from, to);
    }

    @GetMapping("/getAmountsInfo")
    public AmountsInfoResponse amountsInfoResponse() {
        return statisticService.countAmounts();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @GetMapping("/getOrdersAndAppOrdersByPhone")
    public StringResponse getOrdersAndAppOrdersByPhone(@RequestParam Long id) {
        return statisticService.getOrdersAndAppordersByPhone(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping
    public void payAllForOperator(@RequestParam Long userId) {
        statisticService.payAllForOperator(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getRedeliveryStatsByNpAccount")
    public StringResponse getByNpAccount(@RequestParam Long npAccountId, @RequestParam String dateFrom, @RequestParam String dateTo) {
        return statisticService.getRedeliverySumByNpAccountId(npAccountId, dateFrom, dateTo);
    }

    @Autowired
    private GoogleDocsService googleDocsService;

    @PostMapping("updateGoogleTest")
    public void updateTestGoogleFile(@RequestParam String text) {
        googleDocsService.forTest(text);
    }

}
