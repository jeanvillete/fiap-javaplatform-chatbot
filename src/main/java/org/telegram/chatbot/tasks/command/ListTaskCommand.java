package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

class ListTaskCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListTaskCommand.class);

    public ListTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
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
        return "^\\/listar$";
    }

    @Override
    String printHelp() {
        return "/listar [lista todas as tarefas registradas, com seus ids, status e descrições]";
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

                String returningTextMessage = this.getChatSessionManagement().listTasks(payloadCommand.getChatId());
                if (returningTextMessage == null || returningTextMessage.isEmpty()) {
                    returningTextMessage = "Nenhuma atividade foi encontrada na sua sessão...";
                } else {
                    returningTextMessage = "Tarefas registradas;\n\n" + returningTextMessage;
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
