package com.yaskovdev.motivated.stay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
class Task {

    @Id
    @JsonProperty
    private String id;

    private String name;

    private String description;

    private boolean closed;

    public Task() {
    }

    @JsonCreator
    public Task(@JsonProperty("name") final String name, @JsonProperty("description") final String description) {
        this(name, description, false);
    }

    @JsonCreator
    public Task(@JsonProperty("name") final String name, @JsonProperty("description") final String description,
            @JsonProperty("closed") final boolean closed) {
        this.name = name;
        this.description = description;
        this.closed = closed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return closed == task.closed &&
                Objects.equals(id, task.id) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, closed);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", closed=" + closed +
                '}';
    }
}
