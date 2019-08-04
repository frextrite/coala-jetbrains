package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import java.util.HashMap;
import java.util.Map;

public class HighlightIssueFactory implements ProjectComponent {

  private final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = new HashMap<>();

  public HighlightIssueWrapper getComponent(Document document) {
    return highlightIssueWrappers.get(document);
  }

  public void putComponent(Document document, HighlightIssueWrapper highlightIssueWrapper) {
    highlightIssueWrappers.put(document, highlightIssueWrapper);
  }

  public Map<Document, HighlightIssueWrapper> getHighlightIssueWrappers() {
    return this.highlightIssueWrappers;
  }
}
