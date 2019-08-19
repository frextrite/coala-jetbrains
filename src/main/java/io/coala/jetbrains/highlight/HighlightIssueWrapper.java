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

  /**
   * This class holds information about {@link HighlightIssue}, {@link HighlightInfo}
   * and {@link RangeHighlighter}.
   *
   * <p/>
   * {@link RangeHighlighter} instances are creating during actual highlight process and
   * added to the instance.
   *
   * @param project the {@link Project} associated with the file
   * @param document the {@link Document} associated with the file
   * @param psiFile the {@link PsiFile} associated with the file
   * @param highlightIssues the collection associated with current document
   * @param highlightInfos the collection associated with current document
   */
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

  /**
   * Adds a new {@link RangeHighlighter} to the existing collection.
   *
   * @param rangeHighlighter instance to be added to the existing collection
   */
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

  /**
   * Method to remove all {@link HighlightInfo} and {@link RangeHighlighter} created.
   *
   * <p/>
   * Calls {@link RangeHighlighter#dispose()} to dispose off created range highlighters
   * <p/>
   * Clears both the collections thereafter.
   */
  public void clear() {
    for (HighlightInfo highlightInfo : highlightInfos) {
      if (highlightInfo.getHighlighter() == null) {
        continue;
      }
      highlightInfo.getHighlighter().dispose();
    }

    for (RangeHighlighter rangeHighlighter : rangeHighlighters) {
      rangeHighlighter.dispose();
    }

    highlightInfos.clear();
    rangeHighlighters.clear();
  }
}
