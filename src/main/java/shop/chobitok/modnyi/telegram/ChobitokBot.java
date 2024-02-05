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
                sendMessage(chatId, "Choose an option:", createMenuKeyboard());
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

            switch (callbackData) {
                case "Відправки":
                    sendMessage(chatId, "Choose an option:", createSubMenuKeyboard());
                    break;
                case "Чарівно":
                    sendMessage(chatId, orderService.countNeedDeliveryFromDB(true, 1177l).getResult(), null);
                    break;
                case "Модний чобіток":
                    sendMessage(chatId, orderService.countNeedDeliveryFromDB(true, 1175l).getResult(), null);
                    break;
                case "back":
                    sendMessage(chatId, "Choose an option:", createMenuKeyboard());
                    break;
                case "Фінанси":
                    sendMessage(chatId, "Choose a finance option:", createFinanceMenuKeyboard());
                    break;
                case "Добавити витрату":
                    sendMessage(chatId, "Functionality to add an expense will be implemented here.", null);
                    break;
                case "Усі витрати":
                    sendMessage(chatId, "Functionality to view all expenses will be implemented here.", null);
                    break;
            }
        }
    }

    private InlineKeyboardMarkup createMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Відправки").callbackData("Відправки").build());
        row1.add(InlineKeyboardButton.builder().text("Фінанси").callbackData("Фінанси").build());
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

    private InlineKeyboardMarkup createBackKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Back").callbackData("back").build());
        rows.add(row1);

        markup.setKeyboard(rows);
        return markup;
    }

    private void sendMessage(String chatId, String text, InlineKeyboardMarkup replyMarkup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyMarkup).build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            // Consider logging this exception with a logging framework or handling it accordingly
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createFinanceMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("Добавити витрату").callbackData("Добавити витрату").build());
        row1.add(InlineKeyboardButton.builder().text("Усі витрати").callbackData("Усі витрати").build());
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text("Back").callbackData("back").build());
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }
}