package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

abstract class Command implements Runnable {

    private BlockingQueue<PayloadCommand> blockingQueue = new LinkedBlockingQueue<>();

    private TelegramBot telegramBot;
    private CommandsInitializer commandsInitializer;
    private ChatSessionManagement chatSessionManagement;

    Command(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        this.telegramBot = telegramBot;
        this.commandsInitializer = commandsInitializer;
        this.chatSessionManagement = chatSessionManagement;
    }

    // GETTERS AND SETTERS //
    BlockingQueue<PayloadCommand> getBlockingQueue() {
        return blockingQueue;
    }

    TelegramBot getTelegramBot() {
        return telegramBot;
    }

    CommandsInitializer getCommandsInitializer() {
        return commandsInitializer;
    }

    ChatSessionManagement getChatSessionManagement() {
        return chatSessionManagement;
    }

    boolean isItAValidCommand(String plainText) {
        return Pattern
                .compile("^\\/[a-z0-9:]{2,}(\\s.+)*$")
                .matcher(plainText)
                .matches();
    }

    boolean checkRegexForCurrentConcreteCommandInstance(String plainText) {
        return Pattern
                .compile(this.getRegexCommand())
                .matcher(plainText)
                .matches();
    }

    abstract String getRegexCommand();

}
