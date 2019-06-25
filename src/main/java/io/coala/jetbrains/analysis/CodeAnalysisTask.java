package io.coala.jetbrains.analysis;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeAnalysisTask extends Task.Backgroundable {

  private static final Logger LOGGER = Logger.getInstance(CodeAnalysisTask.class);

  public CodeAnalysisTask(@Nullable Project project) {
    super(project, "Code Analysis Task");
  }

  public CodeAnalysisTask(@Nullable Project project, @NotNull String title,
      boolean canBeCancelled) {
    super(project, title, canBeCancelled);
  }

  @Override
  public void run(@NotNull ProgressIndicator progressIndicator) {
    final CodeAnalysisRunner codeAnalysisRunner = myProject.getComponent(CodeAnalysisRunner.class);

    progressIndicator.checkCanceled();

    progressIndicator.setIndeterminate(true);
    progressIndicator.setText("Running coala analysis");
    LOGGER.info("Running coala analysis");
    
    try {
      final ProcessOutput analysisOutput = codeAnalysisRunner.analyze();
      LOGGER.debug(analysisOutput.getStdout());
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    progressIndicator.checkCanceled();

    progressIndicator.setIndeterminate(false);
    progressIndicator.setFraction(.8);
    progressIndicator.setText("coala analysis done!");

    // TODO: Parse the JSON string, show errors/annotate
  }
}
