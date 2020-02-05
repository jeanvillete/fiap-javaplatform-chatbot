package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.exception.ChatNotFoundException;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

class EraseTaskListCommand extends Command {

    public EraseTaskListCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^\\/limpar$";
    }

    @Override
    String printHelp() {
        return "/limpar [zera a lista de tarefas]";
    }

    @Override
    public void run() {
        try {
            PayloadCommand payloadCommand;
            while ((payloadCommand = this.getBlockingQueue().take()) != null) {
                String plainText = payloadCommand.getPlainText();

                if (!isItAValidCommand(plainText)) {
                    continue;
                }
                if (!checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    continue;
                }

                boolean cleaned;
                try {
                    cleaned = this.getChatSessionManagement().cleanTaskList(payloadCommand.getChatId());
                } catch (ChatNotFoundException e) {
                    cleaned = false;
                }

                String returningTextMsg = cleaned ?
                        "Feito, a lista de tarefas foi zerada.":
                        "Não foi encontrado nenhuma tarefa ou lista par ser limpada.";

                this.getTelegramBot().execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                returningTextMsg
                        )
                );
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
