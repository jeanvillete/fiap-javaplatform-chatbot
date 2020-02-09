package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.exception.ChatNotFoundException;
import org.telegram.chatbot.tasks.exception.TaskNotFoundException;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DeleteTaskCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTaskCommand.class);

    public DeleteTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
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
        return "^(\\/remover):([a-z0-9]+)$";
    }

    @Override
    String printHelp() {
        return "/remover:id_tarefa [remove uma tarefa baseado no seu id]";
    }

    @Override
    public void run() {
        try {
            PayloadCommand payloadCommand;
            while ((payloadCommand = this.getBlockingQueue().take()) != null) {
                String plainText = payloadCommand.getPlainText();

                LOGGER.debug("Received a new plainText [{}]", plainText);

                if (!isItAValidCommand(plainText)) {
                    LOGGER.debug("The plainText [{}] is not a valid command.", plainText);
                    continue;
                }
                if (!checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    LOGGER.debug("The plainText [{}] is not valid for current command.", plainText);
                    continue;
                }

                LOGGER.debug("The plainText [{}] looks proper for current command, so inferring content from it.", plainText);

                Matcher matcher = Pattern.compile(getRegexCommand()).matcher(plainText);
                matcher.matches();
                String taskId = matcher.group(2);

                LOGGER.debug("Received taskId [{}] to be removed.", taskId);

                String returningTextMessage;
                try {
                    this.getChatSessionManagement().deleteTask(
                            payloadCommand.getChatId(),
                            taskId
                    );
                    returningTextMessage = "Pronto, tarefa com id [" + taskId + "] removida.";
                } catch (ChatNotFoundException | TaskNotFoundException e) {
                    returningTextMessage = "Tarefa com id [" + taskId + "] não foi removida porque não foi encontrada.";
                }

                LOGGER.debug("Returning response [{}] for chatId [{}]", returningTextMessage, payloadCommand.getChatId());

                this.getTelegramBot().execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                returningTextMessage
                        )
                );
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted Exception raised;", e);
            throw new RuntimeException(e);
        }
    }
}
