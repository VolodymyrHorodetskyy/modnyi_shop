package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.History;
import shop.chobitok.modnyi.entity.HistoryType;
import shop.chobitok.modnyi.service.HistoryService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("history")
public class HistoryController {

    private HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/getLast20")
    public List<History> getLast20(@RequestParam HistoryType type) {
        return historyService.getLast20(type);
    }

}
