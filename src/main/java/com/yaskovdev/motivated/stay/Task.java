package com.yaskovdev.motivated.stay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class Task {

    private final String name;
    private final String description;

    @JsonCreator
    public Task(@JsonProperty("name") final String name, @JsonProperty("description") final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
