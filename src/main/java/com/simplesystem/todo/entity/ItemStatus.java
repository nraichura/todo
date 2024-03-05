package com.simplesystem.todo.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemStatus {
    NOT_DONE("Not Done"),
    DONE("Done"),
    PAST_DUE("Past Due");

    @JsonValue
    private final String description;

    ItemStatus(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}