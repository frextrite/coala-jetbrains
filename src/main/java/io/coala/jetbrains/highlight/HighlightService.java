package io.coala.jetbrains.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.utils.RangeMarkerTextAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

// @TODO: Implement per-file (document) clearing action

public class HighlightService {

  private static final Logger LOGGER = Logger.getInstance(HighlightService.class);

  private int GROUP_ID = 334718;
  private TextAttributes rangeMarkerTextAttributes = ServiceManager
      .getService(RangeMarkerTextAttributes.class).getRangeMarkerTextAttributes();

  /**
   * This method implements clearing of all the custom highlighters created by the plugin.
   *
   * Every {@link Document} is iterated and the corresponding {@link MarkupModel}
   * associated with it is retrieved.
   * <p/>
   * And the highlighters that have been created by the plugin,
   * retrieved by {@link HighlightIssueWrapper#getRangeHighlighters()}
   * are matched with all the range highlighters present in the markup model,
   * and finally disposed off.
   *
   * @param project the project associated
   */
  public void clear(Project project) {
    final HighlightIssueFactory highlightIssueFactory = project
        .getComponent(HighlightIssueFactory.class);

    final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = highlightIssueFactory
        .getHighlightIssueWrappers();

    for (Map.Entry<Document, HighlightIssueWrapper> element : highlightIssueWrappers.entrySet()) {
      final Document document = element.getKey();
      final HighlightIssueWrapper highlightIssueWrapper = element.getValue();

      final MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, false);

      if (markupModel == null) {
        continue;
      }

      final RangeHighlighter[] highlighters = markupModel.getAllHighlighters();
      Arrays.stream(highlighters)
          .filter(highlighter -> highlightIssueWrapper.getRangeHighlighters().contains(highlighter))
          .forEach(RangeMarker::dispose);

      highlightIssueWrapper.clear();
    }
  }

  /**
   * This method is the actual entry point for performing file level highlights.
   *
   * Every {@link Document} is iterated for performing file level highlights.
   * <p/>
   * {@link #addRangeHighlighters(Project, Document, HighlightIssueWrapper)} is used for
   * adding the (pre-created) range markers to the {@link MarkupModel} associated with the Document
   * <p/>
   * {@link #setHighlightersToEditor(Project, Document, Collection)} is used for
   * adding hover-annotations (inspection information) to the Document
   *
   * @param project the project associated
   */
  public void doPerformHighlighting(Project project) {
    final HighlightIssueFactory highlightIssueFactory = project
        .getComponent(HighlightIssueFactory.class);

    final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = highlightIssueFactory
        .getHighlightIssueWrappers();

    for (Map.Entry<Document, HighlightIssueWrapper> element : highlightIssueWrappers.entrySet()) {
      final Document document = element.getKey();
      final HighlightIssueWrapper highlightIssueWrapper = element.getValue();

      final Collection<HighlightInfo> highlightInfos = highlightIssueWrapper.getHighlightInfos();

      ApplicationManager.getApplication()
          .invokeLater(() -> {
            addRangeHighlighters(project, document, highlightIssueWrapper);
            setHighlightersToEditor(project, document, highlightInfos);
          });
    }
  }

  private void addRangeHighlighters(Project project, Document document,
      HighlightIssueWrapper highlightIssueWrapper) {
    final MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);

    final Collection<HighlightIssue> highlightIssues = highlightIssueWrapper.getHighlightIssues();
    for (HighlightIssue highlightIssue : highlightIssues) {
      final RangeHighlighter rangeHighlighter = markupModel
          .addRangeHighlighter(highlightIssue.getStartOffset(), highlightIssue.getEndOffset(),
              HighlighterLayer.ADDITIONAL_SYNTAX, rangeMarkerTextAttributes,
              HighlighterTargetArea.EXACT_RANGE);
      highlightIssueWrapper.addRangeHighlighter(rangeHighlighter);
    }
  }

  private void setHighlightersToEditor(Project project, Document document,
      Collection<HighlightInfo> highlightInfos) {
    UpdateHighlightersUtil
        .setHighlightersToEditor(project, document, 0, document.getTextLength(), highlightInfos,
            null, GROUP_ID);
  }

  /**
   * This method returns the group id of the custom highlighters created.
   *
   * @return the group id of the highlighters
   */
  public int getGroupId() {
    return GROUP_ID;
  }
}
