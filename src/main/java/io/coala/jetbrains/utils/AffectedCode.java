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

  public String getFileName() {
    return fileName;
  }

  public SourceRange getStartSourceRange() {
    return startSourceRange;
  }

  public SourceRange getEndSourceRange() {
    return endSourceRange;
  }

  public String getStartFileName() {
    return startSourceRange.getFile();
  }

  public int getStartLine() {
    return startSourceRange.getLine();
  }

  public int getStartColumn() {
    return startSourceRange.getColumn();
  }

  public String getEndFileName() {
    return endSourceRange.getFile();
  }

  public int getEndLine() {
    return endSourceRange.getLine();
  }

  public int getEndColumn() {
    return endSourceRange.getColumn();
  }
}
