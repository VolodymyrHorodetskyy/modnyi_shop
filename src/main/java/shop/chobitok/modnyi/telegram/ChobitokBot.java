package shop.chobitok.modnyi.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import shop.chobitok.modnyi.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChobitokBot extends TelegramLongPollingBot {

    private final OrderService orderService;

    public ChobitokBot(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String getBotUsername() {
        return "mchobitok_bot";
    }

    @Override
    public String getBotToken() {
        return "5877883629:AAENWWaRFn_Mn31OumRIENAHxh-Albajvy4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (messageText.equals("/start")) {
                sendMenu(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

            switch (callbackData) {
                case "Відправки":
                    sendSubMenu(chatId);
                    break;
                case "Чарівно":
                    sendInfo(chatId, orderService.countNeedDeliveryFromDB(true, 1175l).getResult());
                    break;
                case "Модний чобіток":
                    sendInfo(chatId, orderService.countNeedDeliveryFromDB(true, 1177l).getResult());
                    break;
                case "back":
                    sendMenu(chatId);
                    break;
            }
        }
    }

    private void sendSubMenu(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an option:");
        message.setReplyMarkup(createSubMenuKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenu(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an option:");
        message.setReplyMarkup(createMenuKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private InlineKeyboardMarkup createMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Відправки").callbackData("Відправки").build());
        row1.add(InlineKeyboardButton.builder().text("Option 2").callbackData("option2").build());
        rows.add(row1);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createSubMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Чарівно").callbackData("Чарівно").build());
        row1.add(InlineKeyboardButton.builder().text("Модний чобіток").callbackData("Модний чобіток").build());
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text("Back").callbackData("back").build());
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private void sendInfo(String chatId, String info) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(info);
        message.setReplyMarkup(createBackKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createBackKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Back").callbackData("back").build());
        rows.add(row1);

        markup.setKeyboard(rows);
        return markup;
    }
}