package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdateTaskCommand implements Command {

    private BlockingQueue<PayloadCommand> blockingQueue = new LinkedBlockingQueue<>();
    private TelegramBot telegramBot;

    @Override
    public String getRegexCommand() {
        return "^(\\/alterar):([a-z0-9]+)$";
    }

    @Override
    public BlockingQueue<PayloadCommand> getBlockingQueue() {
        return blockingQueue;
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void run() {
        try {
            PayloadCommand payloadCommand;
            while ((payloadCommand = this.blockingQueue.take()) != null) {
                String plainText = payloadCommand.getPlainText();

                if (!isItAValidCommand(plainText)) {
                    continue;
                }
                if (!checkRegexForCurrentConcreteCommandInstance(plainText)) {
                    continue;
                }

                this.telegramBot.execute(
                        new SendMessage(
                                payloadCommand.getChatId(),
                                "Ok, vou tratar devidament sua requisição para o comando /alterar"
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
