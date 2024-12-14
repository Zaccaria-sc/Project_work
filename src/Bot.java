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
        // Verifica se il messaggio ricevuto è di testo
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendInlineKeyboard(chatId);
            } else if (messageText.equals("/info")) {
                SendMessage response = new SendMessage();
                response.setChatId(chatId); // Aggiungi l'ID della chat per inviare il messaggio nella giusta conversazione
                response.setText("Ciao! Sono integratoriProBot, il tuo assistente personale per trovare le migliori offerte di integratori online. Utilizzo un web scraper avanzato per raccogliere in tempo reale i dati sui prodotti dai siti www.prozis.com e Bulk, leader nel settore degli integratori. Con me al tuo fianco, scoprire le migliori promozioni non è mai stato così facile e veloce!\n" +
                        "\n" +
                        "Il mio compito è aiutarti a navigare tra un mare di offerte, presentandoti solo quelle più vantaggiose e adatte alle tue esigenze. Quando mi chiederai di mostrarti un integratore, ti fornirò una lista dettagliata delle offerte disponibili, completa di prezzi, descrizioni e link diretti. Potrai confrontare le opzioni con semplicità e scegliere quella che fa davvero al caso tuo.\n" +
                        "\n" +
                        "Ma non finisce qui! Se trovi un'offerta che ti entusiasma, potrai aggiungerla al tuo database personale delle offerte. In questo modo, avrai sempre a portata di mano le migliori occasioni, pronte per essere consultate ogni volta che vorrai.\n" +
                        "\n" +
                        "Con integratoriProBot, risparmiare tempo e denaro mentre migliori il tuo benessere non è mai stato così semplice e piacevole. Pronto a iniziare la tua ricerca per le offerte perfette? Io sono qui per aiutarti!\n");
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

            String categoria = null;

            // Identifica la categoria scelta
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
                    System.out.println("Callback sconosciuto: " + callbackData);
            }

            if (categoria != null) {
                // Recupera le offerte dal database
                List<String> offerte = database.getOfferteByCategoria(categoria);

                if (!offerte.isEmpty()) {
                    // Suddividi le offerte in blocchi di testo massimo 4000 caratteri
                    StringBuilder risposta = new StringBuilder("Ecco le migliori offerte per " + categoria + ":\n\n");
                    int charCount = risposta.length();

                    for (String offerta : offerte) {
                        if (charCount + offerta.length() > 4000) {
                            // Invia il blocco corrente
                            SendMessage response = new SendMessage();
                            response.setChatId(chatId);
                            response.setText(risposta.toString());
                            try {
                                execute(response);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

                            // Resetta il buffer
                            risposta.setLength(0);
                            risposta.append("Ecco le migliori offerte per ").append(categoria).append(":\n\n");
                            charCount = risposta.length();
                        }

                        risposta.append(offerta).append("\n\n");
                        charCount += offerta.length();
                    }

                    // Invia il blocco rimanente
                    if (risposta.length() > 0) {
                        SendMessage response = new SendMessage();
                        response.setChatId(chatId);
                        response.setText(risposta.toString());
                        try {
                            execute(response);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // Nessuna offerta disponibile
                    SendMessage response = new SendMessage();
                    response.setChatId(chatId);
                    response.setText("Non ci sono offerte disponibili per " + categoria + ".");
                    try {
                        execute(response);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
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
        return System.getenv("token");
    }
}
