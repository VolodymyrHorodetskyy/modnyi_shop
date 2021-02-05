package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Marking;
import shop.chobitok.modnyi.entity.Ordered;
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
    public List<Marking> getMarkings(@RequestParam(required = false) String ttn,
                                     @RequestParam(required = false) String modelAndColor,
                                     @RequestParam(required = false) Integer size,
                                     @RequestParam(required = false) Boolean showPrinted) {
        return markingService.getMarking(ttn, modelAndColor, size, showPrinted);
    }

    @PatchMapping("/setPrinted")
    public Marking setPrinted(@RequestParam Long id) {
        return markingService.setPrinted(id);
    }

}
