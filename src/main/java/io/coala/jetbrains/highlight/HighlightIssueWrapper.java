package io.coala.jetbrains.highlight;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import java.util.Collection;

public class HighlightIssueWrapper {

  private final Project project;
  private final Document document;
  private final PsiFile psiFile;

  private final Collection<HighlightIssue> highlightIssues;
  private final Collection<RangeMarker> rangeMarkers;

  public HighlightIssueWrapper(Project project,
      Document document, PsiFile psiFile,
      Collection<HighlightIssue> highlightIssues,
      Collection<RangeMarker> rangeMarkers) {
    this.project = project;
    this.document = document;
    this.psiFile = psiFile;
    this.highlightIssues = highlightIssues;
    this.rangeMarkers = rangeMarkers;
  }
}
