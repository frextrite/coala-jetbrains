package io.coala.jetbrains.highlight;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import io.coala.jetbrains.utils.CodeAnalysisIssue.IssueSeverity;

public class HighlightIssue {

  private final String id;
  private final String origin;
  private final String message;
  private final IssueSeverity severity;
  private final RangeMarker range;

  private final Project myProject;
  private final Document document;
  private final PsiFile psiFile;

  public HighlightIssue(String id, String origin, String message, IssueSeverity severity,
      RangeMarker range, Project myProject, Document document, PsiFile psiFile) {
    this.id = id;
    this.origin = origin;
    this.message = message;
    this.severity = severity;
    this.range = range;
    this.myProject = myProject;
    this.document = document;
    this.psiFile = psiFile;
  }

  public HighlightIssue(String origin, String message, IssueSeverity severity,
      RangeMarker range, Project myProject, Document document, PsiFile psiFile) {
    this(null, origin, message, severity, range, myProject, document, psiFile);
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

  public RangeMarker getRange() {
    return range;
  }

  public Project getMyProject() {
    return myProject;
  }

  public Document getDocument() {
    return document;
  }

  public PsiFile getPsiFile() {
    return psiFile;
  }
}
