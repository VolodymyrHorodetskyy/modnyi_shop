package shop.chobitok.modnyi.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Notification;
import shop.chobitok.modnyi.service.NotificationService;

@RestController
@CrossOrigin
@RequestMapping("/notifications")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Page getNotifications(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) Boolean read) {
        return notificationService.getNotifications(page, size, read);
    }

    @PutMapping("/read")
    public Notification makeRead(@RequestParam Long id) {
        return notificationService.makeRead(id);
    }

    @GetMapping("/unreadAmount")
    public Integer getUnread() {
        return notificationService.getUnreadAmount();
    }

}
