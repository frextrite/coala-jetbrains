package io.coala.jetbrains.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;

public final class Notifier {
    private static final String prefix = "coala: ";
    private static final NotificationGroup NOTIFICATION = new NotificationGroup("coala", NotificationDisplayType.BALLOON, true);

    public static void showNotification(Notification notification) {
        ApplicationManager.getApplication().invokeLater(() -> Notifications.Bus.notify(notification));
    }

    public static void showInformationNotification(String message) {
        final Notification notification = NOTIFICATION.createNotification(prefix + message, MessageType.INFO);
        showNotification(notification);
    }

    public static void showWarningNotification(String message) {
        final Notification notification = NOTIFICATION.createNotification(prefix + message, MessageType.WARNING);
        showNotification(notification);
    }

    public static void showErrorNotification(String message) {
        final Notification notification = NOTIFICATION.createNotification(prefix + message, MessageType.ERROR);
        showNotification(notification);
    }
}
