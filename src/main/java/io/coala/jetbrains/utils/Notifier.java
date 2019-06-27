package io.coala.jetbrains.utils;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Notifier {
    private static final String title = "coala";
    private static final NotificationGroup NOTIFICATION = new NotificationGroup("coala", NotificationDisplayType.BALLOON, true);

    public static void showNotification(Notification notification) {
        ApplicationManager.getApplication().invokeLater(() -> Notifications.Bus.notify(notification));
    }

    public static void showInformationNotification(String message) {
        final Notification notification = NOTIFICATION.createNotification(title, message, NotificationType.INFORMATION, null);
        showNotification(notification);
    }

    public static void showWarningNotification(String message) {
        final Notification notification = NOTIFICATION.createNotification(title, message, NotificationType.WARNING, null);
        showNotification(notification);
    }

    public static void showErrorNotification(String message, @Nullable Throwable throwable) {
        final Notification notification = NOTIFICATION.createNotification(title,
                message + "<br/>" + ExceptionUtils.getFullStackTrace(throwable),
                NotificationType.ERROR,
                new NotificationListener.UrlOpeningListener(true))
                .addAction(new NotificationAction("more") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow("Event Log").show(null);
                        });
                    }
                });
        showNotification(notification);
    }
}
