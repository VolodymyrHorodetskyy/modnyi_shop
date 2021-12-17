package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.service.AppOrderToPixelService;

@RestController
@CrossOrigin
@RequestMapping("/AppOrderToPixel")
public class AppOrderToPixelController {

    private AppOrderToPixelService appOrderToPixelService;

    public AppOrderToPixelController(AppOrderToPixelService appOrderToPixelService) {
        this.appOrderToPixelService = appOrderToPixelService;
    }

    @PatchMapping("sendAllTrying0")
    public void sendAllTrying1() {
        appOrderToPixelService.sendAllTrying0();
    }

}
