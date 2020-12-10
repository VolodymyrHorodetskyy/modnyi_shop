package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Card;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
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
    private FinanceService financeService;

    public CardService(CardRepository cardRepository, OrderRepository orderRepository, FinanceService financeService) {
        this.cardRepository = cardRepository;
        this.orderRepository = orderRepository;
        this.financeService = financeService;
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
}
