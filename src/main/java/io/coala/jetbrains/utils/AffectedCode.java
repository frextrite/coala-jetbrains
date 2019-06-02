package io.coala.jetbrains.utils;

public class AffectedCode {
    private final String fileName;
    private final SourceRange startSourceRange;
    private final SourceRange endSourceRange;

    public AffectedCode(String fileName, SourceRange startSourceRange, SourceRange endSourceRange) {
        this.fileName = fileName;
        this.startSourceRange = startSourceRange;
        this.endSourceRange = endSourceRange;
    }

    public String getFileName() { return fileName; }

    public SourceRange getStartSourceRange() { return startSourceRange; }

    public SourceRange getEndSourceRange() { return endSourceRange; }
}
