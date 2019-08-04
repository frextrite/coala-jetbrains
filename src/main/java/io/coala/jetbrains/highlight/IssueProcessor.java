package io.coala.jetbrains.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import io.coala.jetbrains.utils.Notifier;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueProcessor implements ProjectComponent {

  private static Logger LOGGER = Logger.getInstance(IssueProcessor.class);

  private final Project project;
  private final IssueManager issueManager;
  private final HighlightIssueFactory highlightIssueFactory;
  private Map<Document, Collection<RangeMarker>> rangeMarkers = new HashMap<>();
  private Map<Document, Collection<HighlightInfo>> highlightInfos = new HashMap<>();

  public IssueProcessor(Project project, IssueManager issueManager,
      HighlightIssueFactory highlightIssueFactory) {
    this.project = project;
    this.issueManager = issueManager;
    this.highlightIssueFactory = highlightIssueFactory;
  }

  public void submit(List<CodeAnalysisIssue> codeAnalysisIssueList) {
    try {
      populateRangeMarkers(codeAnalysisIssueList);
      populateHighlightInfo(codeAnalysisIssueList);

      final Map<Document, Collection<HighlightIssue>> highlightIssues = getHighlightIssueCollectionDocumentMap(
          codeAnalysisIssueList);

      addHighlightIssueToWrapper(highlightIssues);

      final HighlightService highlightService = ServiceManager.getService(HighlightService.class);
      highlightService.doPerformHighlighting(project);
    } catch (FileNotFoundException e) {
      Notifier.showErrorNotification("Failed to create annotations", e);
      LOGGER.error(e);
    }
  }

  private void addHighlightIssueToWrapper(
      Map<Document, Collection<HighlightIssue>> highlightIssues) {
    final Map<Document, Collection<RangeMarker>> allRangeMarkers = getAllRangeMarkers();

    for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssues
        .entrySet()) {
      final Document document = element.getKey();
      final Collection<HighlightIssue> highlightIssueCollection = element.getValue();

      final HighlightIssueWrapper highlightIssueWrapper = new HighlightIssueWrapper(project,
          document, null, highlightIssueCollection, highlightInfos.get(document));

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

      Collection<HighlightInfo> highlightInfoCollection = createHighlightInfos(
          highlightIssueCollection);

      if (!highlightInfos.containsKey(document)) {
        highlightInfos.put(document, new ArrayList<>());
      }

      highlightInfos.get(document).addAll(highlightInfoCollection);
    }
  }

  private Collection<HighlightInfo> createHighlightInfos(
      Collection<HighlightIssue> highlightIssues) {
    Collection<HighlightInfo> highlightInfoList = new ArrayList<>();

    for (HighlightIssue issue : highlightIssues) {
      highlightInfoList.add(createNewHighlightInfo(issue));
    }

    return highlightInfoList;
  }

  private HighlightInfo createNewHighlightInfo(HighlightIssue issue) {
    return HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        .range(issue.getTextRange())
        .severity(getSeverity(issue))
        .descriptionAndTooltip("coala (" + issue.getOrigin() + "): " + issue.getMessage())
        .needsUpdateOnTyping(false)
        .create();
  }

  private HighlightSeverity getSeverity(HighlightIssue issue) {
    switch (issue.getSeverity()) {
      case INFO:
        return HighlightSeverity.INFORMATION;
      case WARNING:
        return HighlightSeverity.WARNING;
      case ERROR:
        return HighlightSeverity.ERROR;
      default:
        return HighlightSeverity.WEAK_WARNING;
    }
  }

  private void populateRangeMarkers(List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {
    this.rangeMarkers = issueManager.getAllRangeMarkers(codeAnalysisIssueList);
  }

  private Map<Document, Collection<RangeMarker>> getAllRangeMarkers() {
    return this.rangeMarkers;
  }

  private Map<Document, Collection<HighlightIssue>> getHighlightIssueCollectionDocumentMap(
      List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {
    final Map<Document, Collection<HighlightIssue>> highlightIssues = new HashMap<>();

    for (CodeAnalysisIssue issue : codeAnalysisIssueList) {
      final Map<Document, Collection<RangeMarker>> rangeMarkerFromIssue = issueManager
          .getRangeMarkerFromIssue(issue);

      for (Map.Entry<Document, Collection<RangeMarker>> element : rangeMarkerFromIssue.entrySet()) {
        final Document document = element.getKey();
        final Collection<RangeMarker> rangeMarkerCollection = element.getValue();

        for (RangeMarker rangeMarker : rangeMarkerCollection) {
          final HighlightIssue highlightIssue = getNewHighlightIssue(issue, rangeMarker,
              document);

          if (!highlightIssues.containsKey(document)) {
            highlightIssues.put(document, new ArrayList<>());
          }

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
}
