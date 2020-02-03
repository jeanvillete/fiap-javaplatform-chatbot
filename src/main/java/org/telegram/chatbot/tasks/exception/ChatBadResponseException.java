package org.telegram.chatbot.tasks.exception;

public class ChatBadResponseException extends Exception {
    public ChatBadResponseException(String message) {
        super(message);
    }
}
