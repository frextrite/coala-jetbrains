package io.coala.jetbrains.highlight;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import io.coala.jetbrains.utils.CodeAnalysisIssue.IssueSeverity;

public class HighlightIssue {

  private final String id;
  private final String origin;
  private final String message;
  private final IssueSeverity severity;
  private final RangeMarker rangeMarker;

  private final Project myProject;
  private final Document document;

  public HighlightIssue(String id, String origin, String message, IssueSeverity severity,
      RangeMarker rangeMarker, Project myProject, Document document) {
    this.id = id;
    this.origin = origin;
    this.message = message;
    this.severity = severity;
    this.rangeMarker = rangeMarker;
    this.myProject = myProject;
    this.document = document;
  }

  public HighlightIssue(String origin, String message, IssueSeverity severity,
      RangeMarker rangeMarker, Project myProject, Document document) {
    this(null, origin, message, severity, rangeMarker, myProject, document);
  }

  public String getId() {
    return id;
  }

  public String getOrigin() {
    return origin;
  }

  public String getMessage() {
    return message;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RangeMarker getRangeMarker() {
    return rangeMarker;
  }

  public Project getMyProject() {
    return myProject;
  }

  public Document getDocument() {
    return document;
  }

  public TextRange getTextRange() {
    return new TextRange(rangeMarker.getStartOffset(), rangeMarker.getEndOffset());
  }

  public int getStartOffset() {
    return rangeMarker.getStartOffset();
  }

  public int getEndOffset() {
    return rangeMarker.getEndOffset();
  }
}
