import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    Database database = new Database("localhost", "3306", "project_work", "root", "");

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendInlineKeyboard(chatId);
                    break;
                case "/info":
                    sendInfoMessage(chatId);
                    break;
                case "/help":
                    sendHelpMessage(chatId);
                    break;
                default:
                    sendDefaultMessage(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void sendInfoMessage(long chatId) {
        String infoText = "\uD83D\uDC4B *Ciao! Sono IntegratoriProBot!*\n\n" +
                "Sono qui per aiutarti a trovare le migliori offerte di integratori online! \uD83C\uDFCB\uFE0F\u200D♂️\n" +
                "\n" +
                "*Cosa posso fare per te?*\n" +
                "- Mostrarti le offerte più vantaggiose.\n" +
                "- Fornirti dettagli sui prodotti.\n" +
                "- Permetterti di salvare le tue offerte preferite.\n" +
                "\n" +
                "\u2728 Pronto a cominciare? Usa /help per vedere i comandi disponibili!";

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(infoText);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(long chatId) {
        String helpText = "\uD83D\uDCDA *Lista comandi disponibili:*\n\n" +
                "- /start: Avvia il bot e visualizza il menu principale.\n" +
                "- /info: Scopri chi sono e come posso aiutarti.\n" +
                "- /help: Mostra questa guida.";

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(helpText);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDefaultMessage(long chatId) {
        String defaultText = "\u2753 *Non capisco questo comando.* Prova a usare /help per vedere cosa posso fare!";

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(defaultText);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendInlineKeyboard(long chatId) {
        String welcomeText = "\uD83C\uDFCB\uFE0F *Benvenuto in IntegratoriProBot!*\n\n" +
                "Scegli una categoria per vedere le migliori offerte:";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(welcomeText);
        sendMessage.setParseMode("Markdown");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createInlineButton("Proteine \uD83C\uDF7C", "proteine_offerte"));
        row1.add(createInlineButton("Creatina \uD83E\uDEA4", "creatina_offerte"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createInlineButton("Amminoacidi \uD83E\uDDC9", "amminoacidi_offerte"));
        row2.add(createInlineButton("Pre Workout \uD83D\uDCAA", "pre_workout_offerte"));

        keyboard.add(row1);
        keyboard.add(row2);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        String categoria;
        switch (callbackData) {
            case "proteine_offerte":
                categoria = "Proteine";
                break;
            case "creatina_offerte":
                categoria = "Creatina";
                break;
            case "amminoacidi_offerte":
                categoria = "Amminoacidi";
                break;
            case "pre_workout_offerte":
                categoria = "Preworkout";
                break;
            default:
                categoria = null;
        }

        if (categoria != null) {
            List<String> offerte = database.getOfferteByCategoria(categoria);
            sendOffers(chatId, categoria, offerte);
        } else {
            sendDefaultMessage(chatId);
        }
    }

    private void sendOffers(long chatId, String categoria, List<String> offerte) {
        if (offerte.isEmpty()) {
            String noOffersText = "\u274C *Non ci sono offerte disponibili per " + categoria + "!*";

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(noOffersText);
            message.setParseMode("Markdown");

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        for (String offerta : offerte) {
            String offerText = "\uD83D\uDCE2 *Offerta trovata:*\n" + offerta;

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(offerText);
            message.setParseMode("Markdown");

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "IntegratoriProBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }
}
