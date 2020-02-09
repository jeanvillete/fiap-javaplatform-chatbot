package org.telegram.chatbot.tasks;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.CommandsInitializer;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.consumer.ChatMessageConsumer;
import org.telegram.chatbot.tasks.exception.ChatBadResponseException;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

public class ChatApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatApp.class);

    public static void main(String[] args) throws InterruptedException {
        LOGGER.debug("ChatApp just started with arguments [{}]", args);

        if (args == null || args.length != 1) {
            LOGGER.error("Mandatory argument was not provided. Exiting application.");
            throw new IllegalArgumentException("It is expected the \"chatToken\" argument.\n" +
                    "It can be retrieved from telegram when creating the chat bot.");
        }

        LOGGER.debug("Initializing telegram bot session.");
        String botToken = args[0];
        TelegramBot telegramBot = TelegramBotAdapter.build(botToken);

        CommandProducer commandProducer = new CommandProducer();
        new CommandsInitializer(
                commandProducer,
                telegramBot,
                new ChatSessionManagement()
        );

        try {
            new ChatMessageConsumer(
                    telegramBot,
                    commandProducer
            ).consumeIndefinitely();
        } catch (ChatBadResponseException e) {
            LOGGER.error("Error on retrieving data from telegram REST Api", e);
            System.exit(1);
        }

        LOGGER.debug("ChatApp; Concluding main thread.");
    }
}
