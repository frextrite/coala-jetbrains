package io.coala.jetbrains.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueProcessor implements ProjectComponent {

  private final Project project;
  private final IssueManager issueManager;
  private final HighlightIssueFactory highlightIssueFactory;
  private Map<Document, Collection<RangeMarker>> rangeMarkers;
  private Map<Document, Collection<HighlightInfo>> highlightInfos;

  public IssueProcessor(Project project, IssueManager issueManager,
      HighlightIssueFactory highlightIssueFactory) {
    this.project = project;
    this.issueManager = issueManager;
    this.highlightIssueFactory = highlightIssueFactory;
  }

  public void submit(List<CodeAnalysisIssue> codeAnalysisIssueList) {
    // iterate over every issue, create highlight issue and add to document
    // get all range markers

    try {
      populateRangeMarkers(codeAnalysisIssueList);
      populateHighlightInfo(codeAnalysisIssueList);


    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  private void addHighlightIssueToWrapper(
      Map<Document, Collection<HighlightIssue>> highlightIssueDocumentMap) {

    final Map<Document, Collection<RangeMarker>> rangeMarkers = getAllRangeMarkers();

    for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssueDocumentMap
        .entrySet()) {
      final Document document = element.getKey();
      final Collection<HighlightIssue> highlightIssueCollection = element.getValue();

      final HighlightIssueWrapper highlightIssueWrapper = new HighlightIssueWrapper(project,
          document, null, highlightIssueCollection, highlightInfos.get(document),
          rangeMarkers.get(document));

      highlightIssueFactory.putComponent(document, highlightIssueWrapper);
    }
  }

  private void populateHighlightInfo(List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {
    final Map<Document, Collection<HighlightIssue>> highlightIssues = getHighlightIssueCollectionDocumentMap(
        codeAnalysisIssueList);

    for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssues.entrySet()) {
      final Document document = element.getKey();
      final Collection<HighlightIssue> highlightIssueCollection = element.getValue();

      Collection<HighlightInfo> highlightInfos = createHighlightInfos(highlightIssueCollection);

      this.highlightInfos.put(document, highlightInfos);
    }

  }

  private Collection<HighlightInfo> createHighlightInfos(
      Collection<HighlightIssue> highlightIssues) {
    Collection<HighlightInfo> highlightInfos = new ArrayList<>();

    for (HighlightIssue issue : highlightIssues) {
      highlightInfos.add(createNewHighlightInfo(issue));
    }

    return highlightInfos;
  }

  private HighlightInfo createNewHighlightInfo(HighlightIssue issue) {
    return HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR)
        .range(issue.getTextRange())
        .severity(HighlightSeverity.ERROR)
        .descriptionAndTooltip("coala: " + issue.getMessage())
        .needsUpdateOnTyping(false)
        .create();
  }

  private void populateRangeMarkers(List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {
    this.rangeMarkers = issueManager.getAllRangeMarkers(codeAnalysisIssueList);
  }

  private Map<Document, Collection<RangeMarker>> getAllRangeMarkers() {
    return this.rangeMarkers;
  }

  private Map<Document, Collection<HighlightInfo>> getAllHighlightInfos() {
    return this.highlightInfos;
  }

  private Map<Document, Collection<HighlightIssue>> getHighlightIssueCollectionDocumentMap(
      List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {

    final Map<Document, Collection<HighlightIssue>> highlightIssues = new HashMap<>();

    for (CodeAnalysisIssue issue : codeAnalysisIssueList) {
      final Map<Document, Collection<RangeMarker>> rangeMarkers = issueManager
          .getRangeMarkerFromIssue(issue);

      for (Map.Entry<Document, Collection<RangeMarker>> element : rangeMarkers.entrySet()) {
        final Document document = element.getKey();
        final Collection<RangeMarker> rangeMarkerCollection = element.getValue();

        for (RangeMarker rangeMarker : rangeMarkerCollection) {
          final HighlightIssue highlightIssue = getNewHighlightIssue(issue, rangeMarker,
              document);

          highlightIssues.get(document).add(highlightIssue);
        }

      }

    }

    return highlightIssues;
  }

  private HighlightIssue getNewHighlightIssue(CodeAnalysisIssue issue, RangeMarker rangeMarker,
      Document document) {
    return new HighlightIssue(issue.getOrigin(), issue.getMessage(), issue.getSeverity(),
        rangeMarker, project, document);
  }

  public void populateDocuments(List<CodeAnalysisIssue> codeAnalysisIssueList) {

  }
}
