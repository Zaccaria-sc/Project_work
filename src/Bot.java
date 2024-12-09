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

    @Override
    public void onUpdateReceived(Update update) {
        // Verifica se il messaggio ricevuto è di testo
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendInlineKeyboard(chatId);
            } else if (messageText.equals("/info")) {
                SendMessage response = new SendMessage();
                response.setChatId(chatId); // Aggiungi l'ID della chat per inviare il messaggio nella giusta conversazione
                response.setText("Ciao! Sono *integratoriProBot*, il tuo assistente personale per trovare le migliori offerte di integratori online. Utilizzo un web scraper per raccogliere i dati sui prodotti dai siti www.prozis.com e Zec+ Nutrition Italia e li memorizzo in un database. Quando mi chiederai di mostrarti un integratore, ti presenterò le offerte disponibili, permettendoti di scegliere quella che più ti interessa. Se trovi un'offerta che ti piace, puoi aggiungerla al tuo database personale delle offerte per tenerla sempre a portata di mano!");
                response.setParseMode("Markdown"); // Imposta il parse mode a Markdown per la formattazione del testo
                try {
                    execute(response);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(messageText.equals("/help")){
                SendMessage response = new SendMessage();
                response.setChatId(chatId);
                response.setText("/help: mostra i comandi\n/info: Informazioni sul bot\n/start: Avvia il bot");
                response.setParseMode("Markdown");
                try{
                    execute(response);
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Gestisci i callback dei pulsanti
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            if(callbackData.equals("proteine_offerte")) {
                response.setText("Proteine");
            }
            else if(callbackData.equals("creatina_offerte")) {
                response.setText("Creatina");
            }
            else if(callbackData.equals("amminoacidi_offerte")) {
                response.setText("Amminoacidi");
            }
            else if(callbackData.equals("pre_workout_offerte")){
                response.setText("Pre-Workout");
            }
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendInlineKeyboard(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Scegli quale integratore desideri avere le offerte");

// Creazione della tastiera
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

// Riga 1 con due pulsanti
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton proteineButton = new InlineKeyboardButton("Proteine");
        proteineButton.setCallbackData("proteine_offerte");
        row1.add(proteineButton);

        InlineKeyboardButton creatinaButton = new InlineKeyboardButton("Creatina");
        creatinaButton.setCallbackData("creatina_offerte");
        row1.add(creatinaButton);

// Riga 2 con due pulsanti
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton amminoacidiButton = new InlineKeyboardButton("Amminoacidi");
        amminoacidiButton.setCallbackData("amminoacidi_offerte");
        row2.add(amminoacidiButton);

        InlineKeyboardButton preWorkoutButton = new InlineKeyboardButton("Pre Workout");
        preWorkoutButton.setCallbackData("pre_workout_offerte");
        row2.add(preWorkoutButton);

// Aggiunta delle righe alla tastiera
        keyboard.add(row1);
        keyboard.add(row2);

// Impostazione della tastiera al messaggio
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    @Override
    public String getBotUsername() {
        return "IntegratoriProBot"; // Sostituisci con il nome del tuo bot
    }

    @Override
    public String getBotToken() {
        return "7474599356:AAGmz1j9lLmCBmxpJOSYUT_KnPvMVTkSPlA"; // Sostituisci con il token reale
    }
}
