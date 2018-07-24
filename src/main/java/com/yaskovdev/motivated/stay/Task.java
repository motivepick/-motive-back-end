package com.yaskovdev.motivated.stay;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

@Document
class Task {

    @Id
    private String id;

    @NotBlank
    private String userId;

    private String name;

    private String description;

    private Instant instantOfCreation;

    private boolean closed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Instant getInstantOfCreation() {
        return instantOfCreation;
    }

    public void setInstantOfCreation(Instant instantOfCreation) {
        this.instantOfCreation = instantOfCreation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return closed == task.closed &&
                Objects.equals(id, task.id) &&
                Objects.equals(userId, task.userId) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(instantOfCreation, task.instantOfCreation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, description, instantOfCreation, closed);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", instantOfCreation=" + instantOfCreation +
                ", closed=" + closed +
                '}';
    }
}
