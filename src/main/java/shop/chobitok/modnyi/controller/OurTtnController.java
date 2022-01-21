package shop.chobitok.modnyi.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.request.AddOurTtnRequest;
import shop.chobitok.modnyi.entity.request.EditOurTtnRequest;
import shop.chobitok.modnyi.entity.request.ImportOrdersFromStringRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.OurTtnService;


@RestController
@CrossOrigin
@RequestMapping("/ourttn")
public class OurTtnController {

    private OurTtnService ourTtnService;

    public OurTtnController(OurTtnService ourTtnService) {
        this.ourTtnService = ourTtnService;
    }

    @PostMapping("addOurTtn")
    public StringResponse addOurTtn(@RequestBody AddOurTtnRequest request) {
        return ourTtnService.addNewTtn(request);
    }

    @PatchMapping("editOurTtn")
    public StringResponse editOurTtn(@RequestBody EditOurTtnRequest request) {
        return ourTtnService.editOurTtn(request);
    }

    @PostMapping
    public StringResponse getOurTTNS(@RequestBody ImportOrdersFromStringRequest request) {
        return ourTtnService.receive(request);
    }

    @PatchMapping("/updateStatuses")
    public void updateStatuses() {
        ourTtnService.updateStatusesOurTtns();
    }

    @GetMapping
    public Page getOurTtns(@RequestParam int page, @RequestParam int size, @RequestParam boolean showDeletedAndReceived) {
        return ourTtnService.getTtns(page, size, showDeletedAndReceived);
    }
}
