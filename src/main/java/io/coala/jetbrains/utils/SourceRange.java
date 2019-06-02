package io.coala.jetbrains.utils;

public class SourceRange {
    private final String file;
    private final int line;
    private final int column;

    public SourceRange(String file, int line, int column) {
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public String getFile() { return file; }

    public int getLine() { return line; }

    public int getColumn() { return column; }
}
