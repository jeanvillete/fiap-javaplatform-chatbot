package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InsertTaskCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertTaskCommand.class);

    public InsertTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
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
        return "^(\\/nova)\\s(.+)$";
    }

    @Override
    String printHelp() {
        return "/nova descrição tarefa [insere uma nova tarefa]";
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

                Matcher matcher = Pattern.compile(getRegexCommand()).matcher(payloadCommand.getPlainText());
                matcher.matches();
                String taskDescription = matcher.group(2);

                LOGGER.debug("Received task with description [{}] to be added.", taskDescription);

                String taskId = this.getChatSessionManagement().addTask(
                        payloadCommand.getChatId(),
                        taskDescription
                );

                LOGGER.debug(
                        "Task with description [{}] properly stored and generated taskId [{}].",
                        taskDescription,
                        taskId
                );

                String returningTextMessage = "Legal, tarefa inserida e identificada por; " + taskId;

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