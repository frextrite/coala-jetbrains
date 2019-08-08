package io.coala.jetbrains.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import java.util.ArrayList;
import java.util.Collection;

public class HighlightIssueWrapper {

  private final Project project;
  private final Document document;
  private final PsiFile psiFile;

  private final Collection<HighlightIssue> highlightIssues;
  private final Collection<HighlightInfo> highlightInfos;
  private final Collection<RangeHighlighter> rangeHighlighters;

  public HighlightIssueWrapper(Project project,
      Document document, PsiFile psiFile,
      Collection<HighlightIssue> highlightIssues,
      Collection<HighlightInfo> highlightInfos) {
    this.project = project;
    this.document = document;
    this.psiFile = psiFile;
    this.highlightIssues = highlightIssues;
    this.highlightInfos = highlightInfos;
    this.rangeHighlighters = new ArrayList<>();
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

  public Collection<RangeHighlighter> getRangeHighlighters() {
    return rangeHighlighters;
  }

  public void addHighlightIssue(HighlightIssue highlightIssue) {
    highlightIssues.add(highlightIssue);
  }

  public void addRangeHighlighter(RangeHighlighter rangeHighlighter) {
    rangeHighlighters.add(rangeHighlighter);
  }

  public void addHighlightInfo(HighlightInfo highlightInfo) {
    highlightInfos.add(highlightInfo);
  }

  public void addAllHighlightIssues(Collection<HighlightIssue> highlightIssuesCollection) {
    highlightIssues.addAll(highlightIssuesCollection);
  }

  public void addAllRangeHighlighters(Collection<RangeHighlighter> rangeHighlighterCollection) {
    rangeHighlighters.addAll(rangeHighlighterCollection);
  }

  public void addAllHighlightInfos(Collection<HighlightInfo> highlightInfoCollection) {
    highlightInfos.addAll(highlightInfoCollection);
  }

  public void clear() {
    for (HighlightInfo highlightInfo : highlightInfos) {
      highlightInfo.getHighlighter().dispose();
    }

    for (RangeHighlighter rangeHighlighter : rangeHighlighters) {
      rangeHighlighter.dispose();
    }

    highlightInfos.clear();
    rangeHighlighters.clear();
  }
}
