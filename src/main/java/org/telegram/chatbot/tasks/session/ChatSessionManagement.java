package org.telegram.chatbot.tasks.session;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatSessionManagement {

    private Map<Long, ChatSession> chatSessionMap = new ConcurrentHashMap<>();

    public String addTask(Long chatId, String description) {
        Task taskToBeInserted = new Task().setDescription(description);

        this.chatSessionMap.computeIfAbsent(
                chatId,
                _chatId -> new ChatSession()
        ).addTask(taskToBeInserted);

        return taskToBeInserted.getId();
    }

    public String listTasks(Long chatId) {
        return Optional.ofNullable(this.chatSessionMap.get(chatId))
                .map(ChatSession::getTasks)
                .map(Set::stream)
                .orElse(Stream.empty())
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }
}
