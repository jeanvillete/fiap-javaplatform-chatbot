package org.telegram.chatbot.tasks.session;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

class ChatSession {

    private String name;
    private Set<Task> tasks = new TreeSet<>(Comparator.comparing(Task::getRecordTime));

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

    boolean deleteTask(String taskId) {
        return this.tasks.removeIf(task -> task.getId().equals(taskId));
    }

    Set<Task> getTasks() {
        return Collections.unmodifiableSet(this.tasks);
    }

    public void cleanTaskList(Long chatId) {
        this.tasks.clear();
    }
}
