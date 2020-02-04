package org.telegram.chatbot.tasks.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSessionManagement {

    private Map<Long, ChatSession> chatSessionMap = new ConcurrentHashMap<>();

    public void addTask(Long chatId, String description) {
        this.chatSessionMap.computeIfAbsent(
                chatId,
                _chatId -> new ChatSession()
        ).addTask(new Task().setDescription(description));
    }
}
