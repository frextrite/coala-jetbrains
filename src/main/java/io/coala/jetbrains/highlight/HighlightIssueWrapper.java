package io.coala.jetbrains.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
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
  private final Collection<HighlightInfo> highlightInfos;
  private final Collection<RangeMarker> rangeMarkers;

  public HighlightIssueWrapper(Project project,
      Document document, PsiFile psiFile,
      Collection<HighlightIssue> highlightIssues,
      Collection<HighlightInfo> highlightInfos,
      Collection<RangeMarker> rangeMarkers) {
    this.project = project;
    this.document = document;
    this.psiFile = psiFile;
    this.highlightIssues = highlightIssues;
    this.highlightInfos = highlightInfos;
    this.rangeMarkers = rangeMarkers;
  }

  public Project getProject() {
    return project;
  }

  public Document getDocument() {
    return document;
  }

  public PsiFile getPsiFile() {
    return psiFile;
  }

  public Collection<HighlightIssue> getHighlightIssues() {
    return highlightIssues;
  }

  public Collection<HighlightInfo> getHighlightInfos() {
    return highlightInfos;
  }

  public Collection<RangeMarker> getRangeMarkers() {
    return rangeMarkers;
  }

  public void addHighlightIssue(HighlightIssue highlightIssue) {
    highlightIssues.add(highlightIssue);
  }

  public void addRangeMarker(RangeMarker range) {
    rangeMarkers.add(range);
  }

  public void addHighlightInfo(HighlightInfo highlightInfo) {
    highlightInfos.add(highlightInfo);
  }

  public void addAllHighlightIssues(Collection<HighlightIssue> highlightIssuesCollection) {
    highlightIssues.addAll(highlightIssuesCollection);
  }

  public void addAllRangeMarkers(Collection<RangeMarker> rangeMarkerCollection) {
    rangeMarkers.addAll(rangeMarkerCollection);
  }

  public void addAllHighlightInfos(Collection<HighlightInfo> highlightInfoCollection) {
    highlightInfos.addAll(highlightInfoCollection);
  }

  public void clear() {
    for (HighlightInfo highlightInfo : highlightInfos) {
      highlightInfo.getHighlighter().dispose();
    }

    for (RangeMarker rangeMarker : rangeMarkers) {
      rangeMarker.dispose();
    }

    rangeMarkers.clear();
    highlightInfos.clear();
  }
}
