package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class HelpCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);

        LOGGER.debug(
                "Initializing component [{}], which defines as regex [{}] and printHelp [{}]",
                getClass().getName(),
                getRegexCommand(),
                printHelp()
        );
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

                LOGGER.debug("Received a new plainText [{}]", plainText);

                StringBuilder stringBuilder = new StringBuilder();
                if (!isItAValidCommand(plainText)) {
                    stringBuilder
                            .append("Ops, num entendi o que você quis dizer com \"")
                            .append(plainText)
                            .append("\" \n\n");
                }

                if (!anyConcreteCommandMatchesRegex(plainText) || checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    String returningTextMessage = stringBuilder.toString() +
                            getCommandsInitializer().getConcreteInstanceSetOfCommands()
                                    .stream()
                                    .map(Command::printHelp)
                                    .sorted()
                                    .collect(Collectors.joining("\n\n"));

                    LOGGER.debug("Returning response [{}] for chatId [{}]", returningTextMessage, payloadCommand.getChatId());

                    this.getTelegramBot().execute(
                            new SendMessage(
                                    payloadCommand.getChatId(),
                                    returningTextMessage
                            )
                    );
                }
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted Exception raised;", e);
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
