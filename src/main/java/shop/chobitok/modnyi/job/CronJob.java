package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.service.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Service
public class CronJob {

    private final CheckerService checkerService;
    private final OrderService orderService;
    private final CanceledOrderReasonService canceledOrderReasonService;
    private final UserService userService;
    private final AppOrderToPixelService appOrderToPixelService;
    private final AppOrderService appOrderService;

    public CronJob(CheckerService checkerService, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService, UserService userService, AppOrderToPixelService appOrderToPixelService, AppOrderService appOrderService) {
        this.checkerService = checkerService;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.userService = userService;
        this.appOrderToPixelService = appOrderToPixelService;
        this.appOrderService = appOrderService;
    }

    @Scheduled(cron = "0 1 1 * * ?")
    public void dailyJob() {
        orderService.updateOrdersByNovaPosta();
        checkerService.checkPayedKeepingOrders();
        orderService.updateCanceled(25);
        checkerService.checkSendOrdersAndTakeMoreFiveDays();
        orderService.returnAllCanceled(true);
        userService.makeUsersInactive();
    }

    @Scheduled(cron = "0 1 1 * * ?")
    public void dailyJob2() {
        checkerService.makeAppOrderNewAgain();
    }

    @Scheduled(cron = "0 0 */2 * * *")
    public void every2Hours() {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(10));
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
        orderService.returnAllCanceled(true);
    }

    @Scheduled(cron = "0 0/3 * * * *")
    public void checkAppOrdersNeedToBeNewAgain() {
        checkerService.checkRemindOnAppOrdersAndMakeThemNewAgain();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void everyHalfHour() {
        appOrderToPixelService.sendAll(0);
    }

    @Scheduled(cron = "0 0 0 * * TUE,SAT")
    public void everyTueAndSat() {
        checkerService.updateMonthlyReceivingPercentage();
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void every1Minute() throws UnsupportedEncodingException {
        appOrderService.parseDataForAllAppOrders();
    }
}
