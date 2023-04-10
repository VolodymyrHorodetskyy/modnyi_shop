package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.TelegramSubscribers;
import shop.chobitok.modnyi.repository.TelegramSubscribersRepository;

import java.util.List;

@Service
public class TelegramSubscribersService {

    private TelegramSubscribersRepository telegramSubscribersRepository;

    public TelegramSubscribersService(TelegramSubscribersRepository telegramSubscribersRepository) {
        this.telegramSubscribersRepository = telegramSubscribersRepository;
    }

    public TelegramSubscribers save(String chatId) {
        return telegramSubscribersRepository.save(new TelegramSubscribers(chatId));
    }

    public List<TelegramSubscribers> getAllAvailable() {
        return telegramSubscribersRepository.findAllByAvailableTrue();
    }
}
