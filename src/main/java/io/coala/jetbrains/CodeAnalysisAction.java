package io.coala.jetbrains;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.utils.CodeAnalysisRunner;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisAction extends AnAction {
    private final Logger LOGGER = Logger.getInstance(CodeAnalysisAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        final String projectBasePath = project != null ? project.getBasePath() : null;

        if(project == null || projectBasePath == null) {
            LOGGER.debug("Project not opened. Exiting AnAction()");
            return;
        }

        final CodeAnalysisRunner codeAnalysisRunner = project.getComponent(CodeAnalysisRunner.class);

        final Application app = ApplicationManager.getApplication();

        /*app.executeOnPooledThread( () -> {
            try {
                LOGGER.warn("sleeping!");
                Thread.sleep(30000);
                LOGGER.warn("waking");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });*/


        /*app.invokeLater(() -> app.executeOnPooledThread(() -> {
            try {
                codeAnalysisRunner.submit(projectBasePath, "/home/amol/anaconda3/envs/coala/bin/coala", "jetbrains");
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }));*/


        try {
            codeAnalysisRunner.submit(projectBasePath, "/home/amol/anaconda3/envs/coala/bin/coala", "jetbrains");
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }

        /*app.executeOnPooledThread(() -> {
            try {
                codeAnalysisRunner.submit(projectBasePath, "/home/amol/anaconda3/envs/coala/bin/coala", "jetbrains");
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });*/

    }
}
