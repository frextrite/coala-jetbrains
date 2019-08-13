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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IssueProcessor implements ProjectComponent {

  private static Logger LOGGER = Logger.getInstance(IssueProcessor.class);

  private final Project project;
  private final IssueManager issueManager;
  private final HighlightIssueFactory highlightIssueFactory;

  public IssueProcessor(Project project, IssueManager issueManager,
      HighlightIssueFactory highlightIssueFactory) {
    this.project = project;
    this.issueManager = issueManager;
    this.highlightIssueFactory = highlightIssueFactory;
  }

  /**
   * This method is the main entry-point for highlighting procedure.
   *
   * This method processes list of issues and then calls
   * {@link HighlightService#doPerformHighlighting(Project)} for performing actual highlight
   * <p/>
   * {@link CodeAnalysisIssue} list is processed to generate 2 maps containing a
   * per document collection of {@link HighlightIssue} and {@link HighlightInfo}, using which
   * per document instances of {@link HighlightIssueWrapper} are created
   *
   * @param codeAnalysisIssueList the issue list for processing
   */
  public void submit(@Nullable List<CodeAnalysisIssue> codeAnalysisIssueList) {
    if (codeAnalysisIssueList == null) {
      return;
    }

    try {
      final Map<Document, Collection<HighlightIssue>> highlightIssues = getNewHighlightIssues(
          codeAnalysisIssueList);

      final Map<Document, Collection<HighlightInfo>> highlightInfos = getHighlightInfos(
          highlightIssues);

      final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = createHighlightIssueWrappers(
          highlightIssues, highlightInfos);

      addHighlightIssueWrapperToProjectLevelFactory(highlightIssueWrappers);

      final HighlightService highlightService = ServiceManager.getService(HighlightService.class);
      highlightService.doPerformHighlighting(project);
    } catch (FileNotFoundException e) {
      Notifier.showErrorNotification("Failed to create annotations", e);
      LOGGER.error(e);
    }
  }

  private Map<Document, HighlightIssueWrapper> createHighlightIssueWrappers(
      Map<Document, Collection<HighlightIssue>> highlightIssues,
      Map<Document, Collection<HighlightInfo>> highlightInfos) {
    Map<Document, HighlightIssueWrapper> highlightIssueWrapperMap = new HashMap<>();

    for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssues.entrySet()) {
      final Document document = element.getKey();

      final HighlightIssueWrapper highlightIssueWrapper = getNewHighlightIssueWrapper(document,
          highlightIssues.get(document), highlightInfos.get(document));

      highlightIssueWrapperMap.put(document, highlightIssueWrapper);
    }

    return highlightIssueWrapperMap;
  }

  private void addHighlightIssueWrapperToProjectLevelFactory(
      Map<Document, HighlightIssueWrapper> highlightIssueWrapperMap) {
    for (Map.Entry<Document, HighlightIssueWrapper> element : highlightIssueWrapperMap.entrySet()) {
      final Document document = element.getKey();
      final HighlightIssueWrapper highlightIssueWrapper = element.getValue();
      highlightIssueFactory.putComponent(document, highlightIssueWrapper);
    }
  }

  private HighlightIssueWrapper getNewHighlightIssueWrapper(@NotNull Document document,
      Collection<HighlightIssue> highlightIssueCollection,
      Collection<HighlightInfo> highlightInfoCollection) {
    return new HighlightIssueWrapper(project, document, null, highlightIssueCollection,
        highlightInfoCollection);
  }

  private Map<Document, Collection<HighlightInfo>> getHighlightInfos(
      Map<Document, Collection<HighlightIssue>> highlightIssues) {
    final Map<Document, Collection<HighlightInfo>> highlightInfos = new HashMap<>();

    for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssues.entrySet()) {
      final Document document = element.getKey();
      final Collection<HighlightIssue> highlightIssueCollection = element.getValue();

      Collection<HighlightInfo> highlightInfoCollection = createHighlightInfos(
          highlightIssueCollection);

      // TODO: This may be removed
      if (!highlightInfos.containsKey(document)) {
        highlightInfos.put(document, new ArrayList<>());
      }

      highlightInfos.get(document).addAll(highlightInfoCollection);
    }

    return highlightInfos;
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
        .severity(getHighlightSeverity(issue))
        .descriptionAndTooltip(getDescriptionAndTooltip(issue))
        .needsUpdateOnTyping(false)
        .create();
  }

  private String getDescriptionAndTooltip(HighlightIssue issue) {
    String coalaPrefix = "coala";
    String bear = issue.getOrigin();
    String message = issue.getMessage();

    String prefix = coalaPrefix;

    if (bear != null && !bear.isEmpty()) {
      prefix += " (" + bear + ")";
    }

    if (message != null && !message.isEmpty()) {
      return prefix + ": " + message;
    }

    return prefix;
  }

  private HighlightSeverity getHighlightSeverity(HighlightIssue issue) {
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

  private Map<Document, Collection<HighlightIssue>> getNewHighlightIssues(
      List<CodeAnalysisIssue> codeAnalysisIssueList)
      throws FileNotFoundException {
    final Map<Document, Collection<HighlightIssue>> highlightIssues = new HashMap<>();

    for (CodeAnalysisIssue issue : codeAnalysisIssueList) {
      final Map<Document, Collection<HighlightIssue>> highlightIssueCollection = getNewHighlightIssues(
          issue);

      for (Map.Entry<Document, Collection<HighlightIssue>> element : highlightIssueCollection
          .entrySet()) {
        final Document document = element.getKey();
        final Collection<HighlightIssue> rangeMarkerCollection = element.getValue();

        if (!highlightIssues.containsKey(document)) {
          highlightIssues.put(document, new ArrayList<>());
        }

        highlightIssues.get(document).addAll(rangeMarkerCollection);
      }
    }

    return highlightIssues;
  }

  private Map<Document, Collection<HighlightIssue>> getNewHighlightIssues(CodeAnalysisIssue issue)
      throws FileNotFoundException {
    final Map<Document, Collection<RangeMarker>> rangeMarkers = issueManager
        .getRangeMarkerFromIssue(issue);
    final Map<Document, Collection<HighlightIssue>> highlightIssues = new HashMap<>();

    for (Map.Entry<Document, Collection<RangeMarker>> element : rangeMarkers.entrySet()) {
      final Document document = element.getKey();
      final Collection<RangeMarker> rangeMarkerCollection = element.getValue();

      for (RangeMarker rangeMarker : rangeMarkerCollection) {
        final HighlightIssue highlightIssue = getNewHighlightIssue(issue, rangeMarker, document);

        if (!highlightIssues.containsKey(document)) {
          highlightIssues.put(document, new ArrayList<>());
        }

        highlightIssues.get(document).add(highlightIssue);
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
