package com.medlinker.idea.plugin.util;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.medlinker.idea.plugin.listener.MedAppListener;

/**
 * @autho zhangquan
 */
public class LogUtil {

    public static void d(String log) {
        System.out.println(log);
        try {
            if (null != MedAppListener.mProject) {
                ProjectLevelVcsManager.getInstance(MedAppListener.mProject).addMessageToConsoleWindow(log, ConsoleViewContentType.LOG_INFO_OUTPUT);
            }
        } catch (Exception e) {

        }
    }

}
