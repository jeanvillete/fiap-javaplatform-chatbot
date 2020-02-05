package org.telegram.chatbot.tasks.session;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

class Task {

    private String id = UUID.randomUUID().toString().substring(0, 5);
    private String description = "";
    private Boolean done = Boolean.FALSE;
    private LocalDateTime recordTime = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[" + id + "] pronta=" + (done ? "SIM" : "NÃO") + ", descrição=" + description;
    }

    // GETTERS AND SETTERS [default access modifier] //

    String getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    Task setDescription(String description) {
        this.description = description;
        return this;
    }

    Boolean getDone() {
        return done;
    }

    Task setDone(Boolean done) {
        this.done = done;
        return this;
    }

    LocalDateTime getRecordTime() {
        return recordTime;
    }
}
