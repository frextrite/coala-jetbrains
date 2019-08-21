package io.coala.jetbrains;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.analysis.CodeAnalysisExecutor;
import io.coala.jetbrains.analysis.CodeAnalysisTask;
import io.coala.jetbrains.settings.ProjectSettings;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisAction extends AnAction {

  private static final Logger LOGGER = Logger.getInstance(CodeAnalysisAction.class);

  @Override
  public void update(@NotNull AnActionEvent e) {
    super.update(e);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getProject();

    if (project == null || project.isDisposed()) {
      return;
    }

    final ProjectSettings projectSettings = project.getComponent(ProjectSettings.class);
    final CodeAnalysisExecutor executor = project.getComponent(CodeAnalysisExecutor.class);
    final CodeAnalysisTask codeAnalysisTask = new CodeAnalysisTask(project);

    projectSettings.setExecutable("/path/to/coala");
    projectSettings.addSectionToFilter("jetbrains");

    executor.saveAllDocumentsAndRunTask(codeAnalysisTask);
  }
}
