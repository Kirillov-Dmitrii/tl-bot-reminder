package pro.sky.telegrambot.service;


import com.pengrad.telegrambot.request.SendMessage;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;

public class SendMessageServiceImpl implements SendMessageService {
    private final TelegramBotConfiguration telegramBotConfiguration;

    public SendMessageServiceImpl(TelegramBotConfiguration telegramBotConfiguration) {
        this.telegramBotConfiguration = telegramBotConfiguration;
    }

    @Override
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);



    }
}
