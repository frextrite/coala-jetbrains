package io.coala.jetbrains.analysis;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeAnalysisExecutor implements ProjectComponent {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final ProgressManager progressManager;
  private final Project project;

  CodeAnalysisExecutor(Project project, ProgressManager progressManager) {
    this.project = project;
    this.progressManager = progressManager;
  }

  public void runTask(CodeAnalysisTask task) {
    BackgroundableProcessIndicator processIndicator = new BackgroundableProcessIndicator(task);
    executor.submit(() ->
        progressManager.runProcess(() -> task.run(processIndicator), processIndicator));
  }
}
