package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class HelpCommand implements Command {

    private BlockingQueue<PayloadCommand> blockingQueue = new LinkedBlockingQueue<>();
    private TelegramBot telegramBot;

    @Override
    public String getRegexCommand() {
        return "^\\/ajuda$";
    }

    @Override
    public BlockingQueue<PayloadCommand> getBlockingQueue() {
        return blockingQueue;
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void run() {
        try {
            PayloadCommand payloadCommand;
            while ((payloadCommand = this.blockingQueue.take()) != null) {
                String plainText = payloadCommand.getPlainText();

                StringBuilder stringBuilder = new StringBuilder();
                if (!isItAValidCommand(plainText)) {
                    stringBuilder.append("Ops, num entendi o que você quis dizer com \"" + plainText + "\" \n\n");
                }

                if (!anyConcreteCommandMatchesRegex(plainText) || checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    stringBuilder.append("/ajuda [comando para opções possíveis]; \n");

                    this.telegramBot.execute(
                            new SendMessage(
                                    payloadCommand.getChatId(),
                                    stringBuilder.toString() +
                                            INSTANCES.stream()
                                                    .map(Command::getRegexCommand)
                                                    .collect(Collectors.joining("\n"))
                            )
                    );
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean anyConcreteCommandMatchesRegex(String plainText) {
        return INSTANCES.stream()
                .map(Command::getRegexCommand)
                .map(Pattern::compile)
                .map(pattern -> pattern.matcher(plainText))
                .anyMatch(Matcher::matches);
    }
}
