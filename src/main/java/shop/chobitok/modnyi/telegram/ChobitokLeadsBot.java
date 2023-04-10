package shop.chobitok.modnyi.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import shop.chobitok.modnyi.service.TelegramSubscribersService;

@Service
public class ChobitokLeadsBot extends TelegramLongPollingBot {

    private TelegramSubscribersService telegramSubscribersService;

    public ChobitokLeadsBot(TelegramSubscribersService telegramSubscribersService) {
        this.telegramSubscribersService = telegramSubscribersService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()) {
            String command = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            switch (command) {
                case "/subscribe":
                    telegramSubscribersService.save(chatId);
                    String subscribeMessage = "Ви підписались на канал Чобіток Ліди";
                    SendMessage message = new SendMessage(chatId, subscribeMessage);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    // Handle other commands here
                    break;
            }
        } else {
            // Handle other types of updates here
        }
    }

    @Override
    public String getBotUsername() {
        return "mchobitok_leads_bot";
    }

    @Override
    public String getBotToken() {
        return "6214444152:AAEuwzlCE9n4EYj8je3j32NuXZZG_Q-kU3E";
    }

    public void sendMessage(String text, String parseMode) {
        telegramSubscribersService.getAllAvailable().forEach(s -> {
            SendMessage message = new SendMessage();
            message.setChatId(s.getChatId());
            message.setText(text);
            message.setParseMode(parseMode);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }
}