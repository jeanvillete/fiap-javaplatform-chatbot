package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class HelpCommand extends Command {

    public HelpCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^\\/ajuda$";
    }

    @Override
    String printHelp() {
        return "/ajuda [listagem dos comandos disponíveis]";
    }

    @Override
    public void run() {
        try {
            PayloadCommand payloadCommand;
            while ((payloadCommand = this.getBlockingQueue().take()) != null) {
                String plainText = payloadCommand.getPlainText();

                StringBuilder stringBuilder = new StringBuilder();
                if (!isItAValidCommand(plainText)) {
                    stringBuilder
                            .append("Ops, num entendi o que você quis dizer com \"")
                            .append(plainText)
                            .append("\" \n\n");
                }

                if (!anyConcreteCommandMatchesRegex(plainText) || checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    this.getTelegramBot().execute(
                            new SendMessage(
                                    payloadCommand.getChatId(),
                                    stringBuilder.toString() +
                                            getCommandsInitializer().getConcreteInstanceSetOfCommands()
                                                    .stream()
                                                    .map(Command::printHelp)
                                                    .sorted()
                                                    .collect(Collectors.joining("\n\n"))
                            )
                    );
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean anyConcreteCommandMatchesRegex(String plainText) {
        return getCommandsInitializer().getConcreteInstanceSetOfCommands()
                .stream()
                .map(Command::getRegexCommand)
                .map(Pattern::compile)
                .map(pattern -> pattern.matcher(plainText))
                .anyMatch(Matcher::matches);
    }
}
