package ru.javaops.masterjava.model;

public class BatchResult {
    private final String chunk;
    private final String error;

    public BatchResult(String chunk, String error) {
        this.chunk = chunk;
        this.error = error;
    }

    public String getChunk() {
        return chunk;
    }

    public String getError() {
        return error;
    }
}
