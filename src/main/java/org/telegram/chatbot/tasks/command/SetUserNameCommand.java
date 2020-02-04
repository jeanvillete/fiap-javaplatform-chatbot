package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

class SetUserNameCommand extends Command {

    public SetUserNameCommand(TelegramBot telegramBot, CommandsInitializer commandsInitializer, ChatSessionManagement chatSessionManagement) {
        super(telegramBot, commandsInitializer, chatSessionManagement);
    }

    @Override
    String getRegexCommand() {
        return "^(\\/mechamede)\\s(.+)$";
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

                this.getTelegramBot().execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                "Ok, vou tratar devidament sua requisição para o comando /mechamede"
                        )
                );
                // TODO acessar o ChatSessionManagement e obter todos elementos ChatSession presentes que existem para
                // payloadCommand#getChatId, e printar via comando TelegramBot#execute(new SendMessage...)
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
