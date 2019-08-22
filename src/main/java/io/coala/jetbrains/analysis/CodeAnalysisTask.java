package io.coala.jetbrains.analysis;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.highlight.IssueProcessor;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import io.coala.jetbrains.utils.CodeAnalysisLog;
import io.coala.jetbrains.utils.CodeAnalysisLogPrinter;
import io.coala.jetbrains.utils.Notifier;
import io.coala.jetbrains.utils.deserializers.CodeAnalysisIssueDeserializer;
import io.coala.jetbrains.utils.deserializers.CodeAnalysisLogDeserializer;
import java.io.FileNotFoundException;
import java.util.List;
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

    ProcessOutput analysisOutput = null;
    try {
      analysisOutput = codeAnalysisRunner.analyze();
    } catch (ExecutionException | FileNotFoundException e) {
      Notifier.showErrorNotification("Failed to run coala. Make sure the supplied path is "
          + "correct and .coafile exists in project root.", e);
      e.printStackTrace();
    }

    progressIndicator.checkCanceled();

    progressIndicator.setIndeterminate(false);
    progressIndicator.setFraction(.8);
    progressIndicator.setText("coala analysis done!");

    if (analysisOutput == null) {
      Notifier.showErrorNotification("An unknown error occurred.", null);
      return;
    }

    final String jsonResults = analysisOutput.getStdout();

    final List<CodeAnalysisLog> codeAnalysisLogs = CodeAnalysisLogDeserializer
        .getAllCodeAnalysisLogs(jsonResults);

    final CodeAnalysisLogPrinter logPrinter = myProject
        .getComponent(CodeAnalysisLogPrinter.class);
    logPrinter.submit(codeAnalysisLogs);

    final List<CodeAnalysisIssue> codeAnalysisIssues = CodeAnalysisIssueDeserializer
        .getAllCodeAnalysisIssues(jsonResults);
    LOGGER.info("NUMBER OF ERRORS: " + codeAnalysisIssues.size());
    final IssueProcessor issueProcessor = myProject.getComponent(IssueProcessor.class);
    ApplicationManager.getApplication()
        .runReadAction(() -> issueProcessor.submit(codeAnalysisIssues));
  }
}
