package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.service.CanceledOrderReasonService;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.NotificationService;
import shop.chobitok.modnyi.service.OrderService;

import java.time.LocalDateTime;

@Service
public class CronJob {

    private CheckerService checkerService;
    private NotificationService notificationService;
    private OrderService orderService;
    private CanceledOrderReasonService canceledOrderReasonService;


    public CronJob(CheckerService checkerService, NotificationService notificationService, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService) {
        this.checkerService = checkerService;
        this.notificationService = notificationService;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
    }

    //  @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 0 4 * * *")
    public void dailyJob() {
        orderService.updateOrderStatusesNovaPosta();
        checkerService.checkPayedKeepingOrders();
        checkerService.makeAppOrderNewAgain();
        notificationService.createNotification("Чекер спрацював", "", MessageType.CHECKER_WORKED);
    }

    @Scheduled(cron = "0 0 */2 * * *")
    public void every2Hours() {
        canceledOrderReasonService.checkIfWithoutCancelReasonExistsAndCreateDefaultReason(LocalDateTime.now().minusDays(10));
        canceledOrderReasonService.setReturnTtnAndUpdateStatus();
    }

}
