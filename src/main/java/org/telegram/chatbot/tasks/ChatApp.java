package org.telegram.chatbot.tasks;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import org.telegram.chatbot.tasks.command.Command;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.consumer.ChatMessageConsumer;
import org.telegram.chatbot.tasks.exception.ChatBadResponseException;

public class ChatApp {
    public static void main(String[] args) throws ChatBadResponseException, InterruptedException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("It is expected the \"chatToken\" argument.\n" +
                    "It can be retrieved from telegram when creating the chat bot.");
        }
        String botToken = args[0];
        TelegramBot telegramBot = TelegramBotAdapter.build(botToken);

        CommandProducer commandProducer = new CommandProducer();
        Command.hookupListeners(commandProducer, telegramBot);

        new ChatMessageConsumer(telegramBot, commandProducer).consumeIndefinitely();
    }
}
