package io.coala.jetbrains;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisAction extends AnAction {
    private final Logger LOGGER = Logger.getInstance(CodeAnalysisAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
    }
}