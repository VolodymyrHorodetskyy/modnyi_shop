package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.Card;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.entity.response.SavedParamsForNpAccountStats;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.CardService;
import shop.chobitok.modnyi.service.NpAccountService;

import java.util.List;

import static shop.chobitok.modnyi.util.StringHelper.formCardStatsInfo;

@RestController
@CrossOrigin
@RequestMapping("/npAccountAndCard")
public class NpAccountAndCardController {

    private final NpAccountService npAccountService;
    private final CardService cardService;

    public NpAccountAndCardController(NpAccountService npAccountService, CardService cardService) {
        this.npAccountService = npAccountService;
        this.cardService = cardService;
    }

    @GetMapping("/getNpAccounts")
    public List<NpAccount> getNpAccounts() {
        return npAccountService.getAll();
    }

    @GetMapping("/getCards")
    public List<Card> getCards() {
        return cardService.getAll();
    }

    @GetMapping("/cardSpends")
    public StringResponse getCardSpends(@RequestParam Long cardId, @RequestParam Long npAccountId
            , @RequestParam String from, @RequestParam String to) {
        return formCardStatsInfo(cardService.getSumByCardIdAndNpAccountId(cardId, npAccountId, from, to));
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
