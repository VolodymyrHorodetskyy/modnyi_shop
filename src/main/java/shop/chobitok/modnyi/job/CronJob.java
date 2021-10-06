package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.UserService;

import java.time.LocalDateTime;

@Service
public class CronJob {

    private CheckerService checkerService;
    private OrderService orderService;
    private CanceledOrderReasonService canceledOrderReasonService;
    private UserService userService;

    public CronJob(CheckerService checkerService, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService, UserService userService) {
        this.checkerService = checkerService;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.userService = userService;
    }

    //  @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 0 4 * * *")
    public void dailyJob() {
        orderService.updateOrdersByNovaPosta();
        checkerService.checkPayedKeepingOrders();
        checkerService.makeAppOrderNewAgain();
        orderService.updateCanceled();
        checkerService.checkSendOrdersAndTakeMoreFiveDays();
        orderService.returnAllCanceled(true);
        userService.makeUsersInactive();
    }

    @Scheduled(cron = "0 0 */2 * * *")
    public void every2Hours() {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(10));
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
        orderService.updateOrdersByNovaPosta();
    }

    @Scheduled(cron = "0 0/3 * * * *")
    public void checkAppOrdersNeedToBeNewAgain(){
        checkerService.checkRemindOnAppOrdersAndMakeThemNewAgain();
    }

}
