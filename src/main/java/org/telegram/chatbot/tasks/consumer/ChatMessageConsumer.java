package org.telegram.chatbot.tasks.consumer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.exception.ChatBadResponseException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageConsumer.class.getName());
    private static final int LIMIT = 100;

    private Integer offset = 0;
    private TelegramBot telegramBot;
    private CommandProducer commandProducer;

    public ChatMessageConsumer(TelegramBot telegramBot, CommandProducer commandProducer) {
        LOGGER.debug("Initializing component ChatMessageConsumer.");

        this.telegramBot = telegramBot;
        this.commandProducer = commandProducer;
    }

    public void consumeIndefinitely() throws InterruptedException, ChatBadResponseException {
        while (true) {
            consume();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    void consume() throws ChatBadResponseException {
        LOGGER.debug(
                "Invoking telegram REST Api with parameters, limit [{}] and offset [{}]",
                LIMIT,
                this.offset
        );
        GetUpdatesResponse getUpdatesResponse = this.telegramBot.execute(
                new GetUpdates()
                        .limit(LIMIT)
                        .offset(this.offset)
        );

        if (!getUpdatesResponse.isOk()) {
            throw new ChatBadResponseException(
                    "It was not possible retrieve Updates from Telegram REST Api, maybe the provided " +
                            "\"botToken\" is not right/valid."
            );
        }

        Set<PayloadCommand> payloadCommands = Optional.ofNullable(getUpdatesResponse.updates())
                .filter(Objects::nonNull)
                .map(List::stream)
                .get()
                .peek(update -> this.offset = update.updateId() + 1)
                .map(PayloadCommand::new)
                .collect(Collectors.toSet());

        this.commandProducer.produce(payloadCommands);
    }
}
