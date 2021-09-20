package com.sjsu.parknow.model;

public class Post {
    public Post(String operation, String tableName, Payload payload) {
        this.operation = operation;
        this.tableName = tableName;
        this.payload = payload;
    }

    public String operation;
    public String tableName;
    public Payload payload;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
