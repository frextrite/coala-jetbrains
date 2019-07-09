package io.coala.jetbrains.ui;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisToolWindowFactory implements ToolWindowFactory, DumbAware {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    final CodeAnalysisConsoleView codeAnalysisConsoleView = project
        .getComponent(CodeAnalysisConsoleView.class);
    final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    final ConsoleView consoleView = codeAnalysisConsoleView.getConsoleView();
    final Content content = contentFactory
        .createContent(consoleView.getComponent(), "Analysis Log", false);
    toolWindow.getContentManager().addContent(content);
  }
}
