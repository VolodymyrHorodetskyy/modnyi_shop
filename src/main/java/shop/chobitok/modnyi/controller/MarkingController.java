package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.novaposta.entity.MarkingResponse;
import shop.chobitok.modnyi.novaposta.service.MarkingService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/markings")
public class MarkingController {

    private MarkingService markingService;

    public MarkingController(MarkingService markingService) {
        this.markingService = markingService;
    }

    @GetMapping
    public List<MarkingResponse> getMarkings(@RequestParam(required = false) String ttn,
                                             @RequestParam(required = false) String modelAndColor,
                                             @RequestParam(required = false) Integer size) {
        return markingService.getMarking(ttn, modelAndColor, size);
    }
}
