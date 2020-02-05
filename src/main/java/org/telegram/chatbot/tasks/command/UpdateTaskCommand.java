package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.exception.ChatNotFoundException;
import org.telegram.chatbot.tasks.exception.TaskNotFoundException;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UpdateTaskCommand extends Command {

    public UpdateTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^(\\/alterar):([a-z0-9]+)\\s(.+)$";
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
                String taskDescription = matcher.group(3);

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
