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

  /**
   * Creates a new instance of {@link HighlightIssue}.
   *
   * <p/>
   * The instance contains information about the issue:
   * <p/>
   * id
   * origin
   * message
   * severity
   * range marker
   * project
   * document
   *
   * @param id the id of the issue
   * @param origin the originating bear that reported the problem
   * @param message the message shown to the user
   * @param severity the {@link IssueSeverity} of the issue
   * @param rangeMarker the offset range for highlighting in document
   * @param myProject the {@link Project} associated with the current instance
   * @param document the {@link Document} associated with the current instance
   */
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

  /**
   * Creates an instance with no id value.
   *
   * @param origin the originating bear that reported the problem
   * @param message the message shown to the user
   * @param severity the {@link IssueSeverity} of the issue
   * @param rangeMarker the offset marking range for highlighting in document
   * @param myProject the {@link Project} this instance is associated with
   * @param document the {@link Document} this instance is associated with
   * @see #HighlightIssue(String, String, String, IssueSeverity, RangeMarker, Project, Document)
   */
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
