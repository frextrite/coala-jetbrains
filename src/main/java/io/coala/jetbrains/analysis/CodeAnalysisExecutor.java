package io.coala.jetbrains.analysis;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
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

  /**
   * Runs our custom {@link CodeAnalysisTask} synchronously with the specified progress indicator.
   *
   * @param task instance of {@link CodeAnalysisTask} to be run
   * @see ProgressManager#runProcess(Runnable, ProgressIndicator)
   */
  public void runTask(CodeAnalysisTask task) {
    BackgroundableProcessIndicator processIndicator = new BackgroundableProcessIndicator(task);
    executor.submit(() ->
        progressManager.runProcess(() -> task.run(processIndicator), processIndicator));
  }

  /**
   * Saves all the cached documents to the file system and runs the task.
   *
   * <p/>
   * Documents are needed to be saved since coala can only be run on filesystem files
   *
   * @param task instance of {@link CodeAnalysisTask} to be run
   * @see #runTask(CodeAnalysisTask)
   */
  public void saveAllDocumentsAndRunTask(CodeAnalysisTask task) {
    saveAllDocuments();
    runTask(task);
  }

  private void saveAllDocuments() {
    WriteAction.run(() -> FileDocumentManager.getInstance().saveAllDocuments());
  }
}
