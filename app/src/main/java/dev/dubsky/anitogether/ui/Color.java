package dev.dubsky.anitogether.ui;

public enum Color {
    RESET("\u001B[0m"),
    CYAN("\u001B[36m"),
    GREEN("\u001B[32m"),
    RED("\u001B[31m"),
    YELLOW("\u001B[33m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
