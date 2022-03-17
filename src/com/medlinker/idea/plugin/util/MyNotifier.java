package com.medlinker.idea.plugin.util;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import icons.MedPluginIcons;

public class MyNotifier {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Med-Notification-Group", NotificationDisplayType.TOOL_WINDOW, true);

    public static void notify(Project project, String content) {
        Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION);
        notification.setIcon(MedPluginIcons.ICON);
        notification.setTitle("温馨提示");
        notification.notify(project);
    }

    public static void notify(Project project, String content, NotificationType type) {
        Notification notification = NOTIFICATION_GROUP.createNotification(content, type);
        notification.setIcon(MedPluginIcons.ICON);
        notification.setTitle("温馨提示");
        notification.notify(project);
    }

}
