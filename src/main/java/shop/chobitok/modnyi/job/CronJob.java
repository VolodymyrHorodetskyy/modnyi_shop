package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.MessageType;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.MessageService;

@Service
public class CronJob {

    private CheckerService checkerService;
    private MessageService messageService;

    public CronJob(CheckerService checkerService, MessageService messageService) {
        this.checkerService = checkerService;
        this.messageService = messageService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void dailyJob() {
        checkerService.checkCanceledOrders();
        messageService.createMessage("checker worked", MessageType.CHECKER_WORKED);
    }

}
