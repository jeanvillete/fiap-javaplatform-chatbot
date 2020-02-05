package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.exception.ChatNotFoundException;
import org.telegram.chatbot.tasks.exception.TaskNotFoundException;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DeleteTaskCommand extends Command {

    public DeleteTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^(\\/remover):([a-z0-9]+)$";
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

                Matcher matcher = Pattern.compile(getRegexCommand()).matcher(plainText);
                matcher.matches();
                String taskId = matcher.group(2);

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

                this.getTelegramBot().execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                returningTextMessage
                        )
                );
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
