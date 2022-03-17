package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.service.AppOrderToPixelService;

@RestController
@CrossOrigin
@RequestMapping("/AppOrderToPixel")
@PreAuthorize("hasAuthority('ADMIN')")
public class AppOrderToPixelController {

    private final AppOrderToPixelService appOrderToPixelService;

    public AppOrderToPixelController(AppOrderToPixelService appOrderToPixelService) {
        this.appOrderToPixelService = appOrderToPixelService;
    }

    @PatchMapping("sendAllTrying0")
    public void sendAllTrying1() {
        appOrderToPixelService.sendAllTrying0();
    }

    @PatchMapping("sendAll")
    public void sendAll(@RequestParam int tryingGreaterThan, @RequestParam String from) {
        appOrderToPixelService.sendAll(tryingGreaterThan, from);
    }

    @PatchMapping("sendById")
    public void sendById(@RequestParam Long appOrderToPixelId) {
        appOrderToPixelService.sendById(appOrderToPixelId);
    }
}
