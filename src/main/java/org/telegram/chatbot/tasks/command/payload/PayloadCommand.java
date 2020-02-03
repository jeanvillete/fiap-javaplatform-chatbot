package org.telegram.chatbot.tasks.command.payload;

import com.pengrad.telegrambot.model.Update;

import java.util.Objects;
import java.util.Optional;

public class PayloadCommand {
    private Long chatId;
    private String plainText;

    public PayloadCommand(Update chatUpdate) {
        this.chatId = chatUpdate.message().chat().id();
        this.plainText = Optional.ofNullable(chatUpdate.message().text()).map(String::trim).orElse("");
    }

    public Long getChatId() {
        return chatId;
    }

    public String getPlainText() {
        return plainText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayloadCommand that = (PayloadCommand) o;
        return plainText.equals(that.plainText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plainText);
    }

    @Override
    public String toString() {
        return "PayloadCommand{" +
                "chatId=" + chatId +
                ", plainText='" + plainText + '\'' +
                '}';
    }
}
