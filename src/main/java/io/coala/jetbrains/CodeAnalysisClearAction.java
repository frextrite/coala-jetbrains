package io.coala.jetbrains;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.highlight.HighlightService;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisClearAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = e.getProject();

    if (project == null) {
      return;
    }

    final HighlightService highlightService = ServiceManager.getService(HighlightService.class);

    highlightService.clear(project);
  }
}
