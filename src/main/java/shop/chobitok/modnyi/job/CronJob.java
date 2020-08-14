package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.NotificationService;
import shop.chobitok.modnyi.service.OrderService;

@Service
public class CronJob {

    private CheckerService checkerService;
    private NotificationService notificationService;
    private OrderService orderService;


    public CronJob(CheckerService checkerService, NotificationService notificationService, OrderService orderService) {
        this.checkerService = checkerService;
        this.notificationService = notificationService;
        this.orderService = orderService;
    }

    //  @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 0 4 * * *")
    public void dailyJob() {
        //    checkerService.checkCanceledOrders();
        orderService.updateOrderStatusesNovaPosta();
        checkerService.checkPayedKeepingOrders();
 //       checkerService.checkCanceledOrders();
        checkerService.makeAppOrderNewAgain();
        notificationService.createNotification("Чекер спрацював", "", MessageType.CHECKER_WORKED);
    }

}
