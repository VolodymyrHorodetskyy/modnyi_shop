package shop.chobitok.modnyi.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import shop.chobitok.modnyi.entity.VariantType;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.entity.request.SaveAdsSpendsRequest;
import shop.chobitok.modnyi.service.CostsService;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.VariantsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChobitokBot extends TelegramLongPollingBot {

    private final OrderService orderService;
    private final CostsService costsService;
    private final VariantsService variantsService;
    private final Map<String, SaveAdsSpendsRequest> inputCollector = new HashMap<>();

    public ChobitokBot(OrderService orderService, CostsService costsService, VariantsService variantsService) {
        this.orderService = orderService;
        this.costsService = costsService;
        this.variantsService = variantsService;
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
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();
            handleTextMessage(chatId, messageText);
        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String callbackData = update.getCallbackQuery().getData();
            handleCallbackQuery(chatId, callbackData);
        }
    }

    private void handleTextMessage(String chatId, String messageText) {
        if (!inputCollector.containsKey(chatId)) {
            if ("/start".equals(messageText)) {
                sendMessage(chatId, "Choose an option:", createMenuKeyboard());
            } else {
                sendMessage(chatId, "Sorry, I didn't understand that command.", null);
            }
        } else {
            SaveAdsSpendsRequest request = inputCollector.get(chatId);
            processInput(chatId, messageText, request);
        }
    }

    private void handleCallbackQuery(String chatId, String callbackData) {
        if (callbackData.startsWith("SPEND_TYPE_")) {
            String spendTypeIdStr = callbackData.replace("SPEND_TYPE_", "");
            Long spendTypeId = Long.parseLong(spendTypeIdStr);
            SaveAdsSpendsRequest request = inputCollector.get(chatId);
            if (request != null) {
                request.setSpendTypeId(spendTypeId);
                sendMessage(chatId, "Please enter the description:", null);
            }
        } else {
            switch (callbackData) {
                case "Відправки":
                    sendMessage(chatId, "Chose a brand:", createSubMenuKeyboard());
                    break;
                case "Фінанси":
                    sendMessage(chatId, "Choose a finance option:", createFinanceMenuKeyboard());
                    break;
                case "Добавити витрату":
                    startCollectingInputs(chatId);
                    break;
                case "Чарівно":
                    sendMessage(chatId, orderService.countNeedDeliveryFromDB(true, 1177l).getResult(),  createBackButtonMarkup());
                    break;
                case "Модний чобіток":
                    sendMessage(chatId, orderService.countNeedDeliveryFromDB(true, 1175l).getResult(),  createBackButtonMarkup());
                    break;
                case "back":
                    sendMessage(chatId, "Choose an option:", createMenuKeyboard());
                    break;
                case "Усі витрати":
                    sendMessage(chatId, "Functionality to view all expenses will be implemented here.", null);
                    break;

            }
        }
    }

    private void startCollectingInputs(String chatId) {
        SaveAdsSpendsRequest request = new SaveAdsSpendsRequest();
        inputCollector.put(chatId, request);
        sendMessage(chatId, "Please enter the start date (YYYY-MM-DD):", null);
    }

    private void processInput(String chatId, String input, SaveAdsSpendsRequest request) {
        // Simplified process for demonstration. Implement validation and more sophisticated state management in production.
        if (request.getStart() == null) {
            request.setStart(input);
            sendMessage(chatId, "Please enter the end date (YYYY-MM-DD):", null);
        } else if (request.getEnd() == null) {
            request.setEnd(input);
            sendMessage(chatId, "Please enter the spend amount:", null);
        } else if (request.getSpends() == null) {
            try {
                Double spends = Double.parseDouble(input);
                request.setSpends(spends);
                sendSpendTypeSelection(chatId);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Invalid number. Please enter the spend amount again:", null);
            }
        } else if (request.getSpendTypeId() == null) {
            try {
                Long spendTypeId = Long.parseLong(input);
                request.setSpendTypeId(spendTypeId);
                //    sendMessage(chatId, "Please enter the description:", null);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Invalid ID. Please enter the spend type ID again:", null);
            }
        } else {
            request.setDescription(input);
            costsService.addOrEditRecord(inputCollector.get(chatId));
            inputCollector.remove(chatId);

            sendMessage(chatId, "Expense added successfully!", null);
            sendMessage(chatId, "Choose a finance option:", createFinanceMenuKeyboard());
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

    private void sendMessage(String chatId, String text, InlineKeyboardMarkup replyMarkup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyMarkup).build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSpendTypeSelection(String chatId) {
        List<Variants> spendTypes = variantsService.getByType(VariantType.CostsType);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Variants variant : spendTypes) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder()
                    .text(variant.getGetting())
                    .callbackData("SPEND_TYPE_" + variant.getId()).build());
            rows.add(row);
        }

        markup.setKeyboard(rows);
        sendMessage(chatId, "Please select the spend type:", markup);
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

    private InlineKeyboardMarkup createBackButtonMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Back").callbackData("back").build());
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }
}