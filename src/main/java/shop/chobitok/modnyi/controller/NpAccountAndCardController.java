package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Card;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.SavedParamsForNpAccountStats;
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

    @GetMapping("/getNpAccounts")
    public List<NpAccount> getNpAccounts() {
        return propsService.getAll();
    }

    @GetMapping("/getCards")
    public List<Card> getCards() {
        return cardService.getAll();
    }

    @GetMapping("/cardSpends")
    public StringResponse getCardSpends(@RequestParam Long cardId) {
        return formCardStatsInfo(cardService.getSumByCardId(cardId));
    }

    @GetMapping("/getActualCardSum")
    public EarningsResponse getActualCardSum() {
        return cardService.getSumByActualCard();
    }

    @GetMapping("/getActualCard")
    public Card getActualCard() {
        return cardService.getActualCard();
    }

    @GetMapping("/getSaveParamsForNpAccount")
    public SavedParamsForNpAccountStats getParamsForNpAccountStats() {
        return cardService.getParamsForNpAccountStat();
    }

}
