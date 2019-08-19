package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import java.util.HashMap;
import java.util.Map;

public class HighlightIssueFactory implements ProjectComponent {

  private final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = new HashMap<>();

  /**
   * This method returns an instance of {@link HighlightIssueWrapper}
   * corresponding to the supplied document.
   *
   * @param document the document instance corresponding to which
   *     HighlightIssueWrapper data is required
   * @return instance of {@link HighlightIssueWrapper}
   */
  public HighlightIssueWrapper getComponent(Document document) {
    return highlightIssueWrappers.get(document);
  }

  /**
   * This method adds an instance of given {@link HighlightIssueWrapper} to the
   * global map with key as given instance of {@link Document}.
   *
   * @param document the document corresponding to the object
   * @param highlightIssueWrapper the actual object to be stored
   */
  public void putComponent(Document document, HighlightIssueWrapper highlightIssueWrapper) {
    highlightIssueWrappers.put(document, highlightIssueWrapper);
  }

  /**
   * This method returns all the {@link HighlightIssueWrapper} instances with
   * their corresponding documents.
   *
   * @return map with keys as {@link Document} and value as {@link HighlightIssueWrapper}
   */
  public Map<Document, HighlightIssueWrapper> getHighlightIssueWrappers() {
    return this.highlightIssueWrappers;
  }
}
