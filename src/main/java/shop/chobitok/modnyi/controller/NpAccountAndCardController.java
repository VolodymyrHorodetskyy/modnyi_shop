package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.CardService;
import shop.chobitok.modnyi.service.PropsService;

import java.util.List;

import static shop.chobitok.modnyi.util.StringHelper.formCardStatsInfo;

@RestController
@CrossOrigin
@RequestMapping("/npAccountAndCard")
public class NpAccountAndCardController {

    private PropsService propsService;
    private CardService cardService;

    public NpAccountAndCardController(PropsService propsService, CardService cardService) {
        this.propsService = propsService;
        this.cardService = cardService;
    }

    @GetMapping
    public List<NpAccount> getAll() {
        return propsService.getAll();
    }

    @GetMapping("/cardSpends")
    public StringResponse getCardSpends(@RequestParam Long cardId) {
        return formCardStatsInfo(cardService.getSumByCardId(cardId));
    }

}
