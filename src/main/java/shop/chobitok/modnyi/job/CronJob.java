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


    public CronJob(CheckerService checkerService, NotificationService notificationService) {
        this.checkerService = checkerService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void dailyJob() {
        //    checkerService.checkCanceledOrders();
        orderService.updateOrderStatuses();
        notificationService.createMessage("checker worked", MessageType.CHECKER_WORKED);
    }

}
