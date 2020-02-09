package org.telegram.chatbot.tasks.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.exception.ChatNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatSessionManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSessionManagement.class.getName());

    private Map<Long, ChatSession> chatSessionMap = new ConcurrentHashMap<>();

    public ChatSessionManagement() {
        LOGGER.debug("Initializing component ChatSessionManagement.");
    }

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

    public void deleteTask(Long chatId, String taskId) {
        ChatSession chatSession = this.chatSessionMap.get(chatId);
        if (chatSession == null) {
            throw new ChatNotFoundException("No chat session instance was found for chatId=[" + chatId + "]");
        }

        chatSession.deleteTask(taskId);
    }

    public boolean cleanTaskList(Long chatId) {
        ChatSession chatSession = this.chatSessionMap.get(chatId);
        if (chatSession == null) {
            throw new ChatNotFoundException("No chat session instance was found for chatId=[" + chatId + "]");
        }
        chatSession.cleanTaskList(chatId);
        return true;
    }

    public void markTaskAsDone(Long chatId, String taskId) {
        ChatSession chatSession = this.chatSessionMap.get(chatId);
        if (chatSession == null) {
            throw new ChatNotFoundException("No chat session instance was found for chatId=[" + chatId + "]");
        }

        chatSession.markTaskAsDone(taskId);
    }

    public void markTaskAsUndone(Long chatId, String taskId) {
        ChatSession chatSession = this.chatSessionMap.get(chatId);
        if (chatSession == null) {
            throw new ChatNotFoundException("No chat session instance was found for chatId=[" + chatId + "]");
        }

        chatSession.markTaskAsUndone(taskId);
    }

    public void updateTaskDescription(Long chatId, String taskId, String taskDescription) {
        ChatSession chatSession = this.chatSessionMap.get(chatId);
        if (chatSession == null) {
            throw new ChatNotFoundException("No chat session instance was found for chatId=[" + chatId + "]");
        }

        chatSession.updateTaskDescription(taskId, taskDescription);
    }
}
