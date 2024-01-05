package shop.chobitok.modnyi.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.service.*;
import shop.chobitok.modnyi.service.horoshop.HoroshopService;
import shop.chobitok.modnyi.service.horoshop.mapper.AppOrderHoroshopMapper;
import shop.chobitok.modnyi.telegram.ChobitokLeadsBot;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CronJob {

    private final CheckerService checkerService;
    private final OrderService orderService;
    private final CanceledOrderReasonService canceledOrderReasonService;
    private final UserService userService;
    private final AppOrderToPixelService appOrderToPixelService;
    private final AppOrderService appOrderService;
    private final AppOrderHoroshopMapper appOrderHoroshopMapper;
    private final HoroshopService horoshopService;
    private final ChobitokLeadsBot chobitokLeadsBot;

    public CronJob(CheckerService checkerService, OrderService orderService, CanceledOrderReasonService canceledOrderReasonService, UserService userService, AppOrderToPixelService appOrderToPixelService, AppOrderService appOrderService, AppOrderHoroshopMapper appOrderHoroshopMapper, HoroshopService horoshopService, ChobitokLeadsBot chobitokLeadsBot) {
        this.checkerService = checkerService;
        this.orderService = orderService;
        this.canceledOrderReasonService = canceledOrderReasonService;
        this.userService = userService;
        this.appOrderToPixelService = appOrderToPixelService;
        this.appOrderService = appOrderService;
        this.appOrderHoroshopMapper = appOrderHoroshopMapper;
        this.horoshopService = horoshopService;
        this.chobitokLeadsBot = chobitokLeadsBot;
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

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void scheduleTaskEveryTenMinutes() {
        List<AppOrder> appOrders = appOrderHoroshopMapper.convertToAppOrderFilteringExistingAppOrders(
                horoshopService.getOrderData(LocalDateTime.now().minusHours(1), null, null));
        if (appOrders != null) {
            appOrders.forEach(appOrder -> chobitokLeadsBot.sendMessage(mapToTelegramLead(appOrder), ParseMode.HTML));
            appOrderService.saveAll(appOrders);
        }
    }

    private String mapToTelegramLead(AppOrder appOrder) {
        return String.format("<b>Нове замовлення:</b>\n\n"
                        + "<b>Ім'я:</b> %s\n"
                        + "<b>Телефон:</b> <a href=\"tel:%s\">%s</a>\n"
                        + "<b>Подзвонити:</b> %s\n"
                        + "<b>Продукти:</b> %s\n"
                        + "<b>Дані по доставці:</b> %s",
                appOrder.getName(), appOrder.getPhone(), appOrder.getPhone(),
                appOrder.isDontCall() ? "ні" : "так",
                appOrder.getHoroshopProductsJson(), appOrder.getHoroshopDeliveryDataJson());
    }
}
