package io.coala.jetbrains.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener.UrlOpeningListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Notifier {

  private static final String title = "coala";
  private static final NotificationGroup NOTIFICATION = new NotificationGroup("coala",
      NotificationDisplayType.BALLOON,
      true);

  public static void showNotification(Notification notification) {
    ApplicationManager.getApplication().invokeLater(() -> Notifications.Bus.notify(notification));
  }

  public static void showInformationNotification(String message) {
    final Notification notification = NOTIFICATION
        .createNotification(title, message, NotificationType.INFORMATION, null);
    showNotification(notification);
  }

  public static void showWarningNotification(String message) {
    final Notification notification = NOTIFICATION
        .createNotification(title, message, NotificationType.WARNING, null);
    showNotification(notification);
  }

  public static void showErrorNotification(String message, @Nullable Throwable throwable) {
    final UrlOpeningListener urlOpeningListener = new UrlOpeningListener(true);

    final Notification notification = NOTIFICATION.createNotification(title,
        message + "<br/>" + ExceptionUtils.getFullStackTrace(throwable),
        NotificationType.ERROR,
        urlOpeningListener)
        .addAction(showEventLogToolWindowAction());

    showNotification(notification);
  }

  private static NotificationAction showEventLogToolWindowAction() {
    final String actionText = "more";

    return new NotificationAction(actionText) {
      @Override
      public void actionPerformed(@NotNull AnActionEvent anActionEvent,
          @NotNull Notification notification) {
        ApplicationManager.getApplication().invokeLater(() ->
            ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow("Event Log")
                .show(null));
      }
    };
  }
}
