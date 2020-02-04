package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InsertTaskCommand extends Command {

    public InsertTaskCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^(\\/nova)\\s(.+)$";
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

                Matcher matcher = Pattern.compile(getRegexCommand()).matcher(payloadCommand.getPlainText());
                matcher.matches();
                String taskDescription = matcher.group(2);

                String taskId = this.getChatSessionManagement().addTask(
                        payloadCommand.getChatId(),
                        taskDescription
                );

                this.getTelegramBot().execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                "Legal, tarefa inserida e identificada por; " + taskId
                        )
                );
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}