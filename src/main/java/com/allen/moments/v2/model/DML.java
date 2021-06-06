package com.allen.moments.v2.model;

public enum DML {
    UPDATE("update"),
    INSERT("insertion"),
    DELETE("deletion");

    public final String description;

    DML (String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DML{" +
                "description='" + description + '\'' +
                '}';
    }
}
