package org.telegram.chatbot.tasks.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.exception.TaskNotFoundException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

class ChatSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSession.class);

    private String name;
    private Set<Task> tasks = new TreeSet<>(Comparator.comparing(Task::getRecordTime));

    public ChatSession() {
        LOGGER.debug("Initializing component ChatSession.");
    }

    String getName() {
        return name;
    }

    ChatSession setName(String name) {
        this.name = name;
        return this;
    }

    ChatSession addTask(Task task) {
        this.tasks.add(task);
        return this;
    }

    void deleteTask(String taskId) {
        boolean removedAnyItem = this.tasks.removeIf(task -> task.getId().equals(taskId));
        if (!removedAnyItem) {
            throw new TaskNotFoundException("No task was found for the provided id = [" + taskId + "]");
        }
    }

    Set<Task> getTasks() {
        return Collections.unmodifiableSet(this.tasks);
    }

    void cleanTaskList(Long chatId) {
        this.tasks.clear();
    }

    void markTaskAsDone(String taskId) {
        this.tasks.stream()
                .filter(_task -> _task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("No task was found for the provided id = [" + taskId + "]"))
                .setDone(Boolean.TRUE);
    }

    void markTaskAsUndone(String taskId) {
        this.tasks.stream()
                .filter(_task -> _task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("No task was found for the provided id = [" + taskId + "]"))
                .setDone(Boolean.FALSE);
    }

    void updateTaskDescription(String taskId, String taskDescription) {
        this.tasks.stream()
                .filter(_task -> _task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("No task was found for the provided id = [" + taskId + "]"))
                .setDescription(taskDescription);
    }
}
