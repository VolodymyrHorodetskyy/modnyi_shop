package shop.chobitok.modnyi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Card;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Params;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.SavedParamsForNpAccountStats;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.repository.CardRepository;
import shop.chobitok.modnyi.repository.OrderRepository;

import java.util.List;
import java.util.Map;

import static shop.chobitok.modnyi.util.OrderHelper.breakdownByStatuses;

@Service
public class CardService {

    private CardRepository cardRepository;
    private OrderRepository orderRepository;
    private ParamsService paramsService;

    @Value("${params.actualCardParam}")
    private String actualCardParamName;
    @Value("${params.dateFromNpAccountSearch}")
    private String dateFromParamName;
    @Value("${params.dateToNpAccountSearch}")
    private String dateToParamName;

    public CardService(CardRepository cardRepository, OrderRepository orderRepository, ParamsService paramsService) {
        this.cardRepository = cardRepository;
        this.orderRepository = orderRepository;
        this.paramsService = paramsService;
    }

    public Card getOrSaveAndGetCardByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Card card = cardRepository.findByCardMask(name);
        if (card == null) {
            card = new Card();
            card.setCardMask(name);
            card = cardRepository.save(card);
        }
        paramsService.saveOrChangeParam(actualCardParamName, card.getId().toString());
        return card;
    }

    public Card getOrSaveAndGetCardByName(DataForList.RedeliveryPaymentCard redeliveryPaymentCard) {
        Card card = null;
        if (redeliveryPaymentCard != null) {
            card = getOrSaveAndGetCardByName(redeliveryPaymentCard.getCardMaskedNumber());
        }
        return card;
    }

    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    @Transactional
    public EarningsResponse getSumByCardId(Long id) {
        List<Ordered> orderedList = orderRepository.findByCardId(id);
        Map<Status, List<Ordered>> statusListMap = breakdownByStatuses(orderedList);
        Double predictedSum = 0d;
        Double sum = 0d;
        for (Map.Entry<Status, List<Ordered>> statusListEntry : statusListMap.entrySet()) {
            Status status = statusListEntry.getKey();
            List<Ordered> ordereds = statusListEntry.getValue();
            for (Ordered ordered : ordereds) {
                if (status == Status.СТВОРЕНО || status == Status.ДОСТАВЛЕНО || status == Status.ВІДПРАВЛЕНО) {
                    predictedSum += ordered.getReturnSumNP();
                } else if (status == Status.ОТРИМАНО) {
                    sum += ordered.getReturnSumNP();
                }
            }
        }
        Double realisticSum = (predictedSum / 100) * 80;
        return new EarningsResponse(sum, predictedSum, realisticSum);
    }

    public EarningsResponse getSumByActualCard() {
        Params params = paramsService.getParam(actualCardParamName);
        if (params != null) {
            return getSumByCardId(Long.parseLong(params.getGetting()));
        }
        return null;
    }

    public Card getActualCard() {
        Params params = paramsService.getParam(actualCardParamName);
        if (params != null) {
            return cardRepository.findById(Long.parseLong(params.getGetting())).orElse(null);
        }
        return null;
    }

    public SavedParamsForNpAccountStats getParamsForNpAccountStat() {
        return new SavedParamsForNpAccountStats(
                paramsService.getActualNpAccountId(), paramsService.getParam(dateFromParamName).getGetting(),
                paramsService.getParam(dateToParamName).getGetting());
    }

}
