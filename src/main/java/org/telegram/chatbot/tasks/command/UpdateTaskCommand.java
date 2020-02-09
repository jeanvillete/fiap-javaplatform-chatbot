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

class UpdateTaskCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTaskCommand.class);

    public UpdateTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
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
        return "^(\\/alterar):([a-z0-9]+)\\s(.+)$";
    }

    @Override
    String printHelp() {
        return "/alterar:id_tarefa novo texto [altera a descrição de uma tarefa baseado no seu id]";
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
                String taskDescription = matcher.group(3);

                LOGGER.debug("Received taskId [{}] along with task description [{}] to be updated.", taskId, taskDescription);

                String returningTextMessage;
                try {
                    this.getChatSessionManagement().updateTaskDescription(
                            payloadCommand.getChatId(),
                            taskId,
                            taskDescription
                    );

                    returningTextMessage = "Sucesso, atividade com id = [" + taskId + "] teve sua descrição atualizada para; " + taskDescription;
                } catch (ChatNotFoundException | TaskNotFoundException e) {
                    returningTextMessage = "Tarefa com id [" + taskId + "] não foi encontrada, portando não foi marcada como pronta.";
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
