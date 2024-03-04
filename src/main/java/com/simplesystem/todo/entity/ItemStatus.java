package com.simplesystem.todo.entity;

public enum ItemStatus {
        NOT_DONE("Not Done"),
        DONE("Done"),
        PAST_DUE("Past Due");

        private final String description;

        ItemStatus(final String description) {
            this.description = description;
        }
    }