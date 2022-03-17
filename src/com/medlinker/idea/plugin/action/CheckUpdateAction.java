package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.util.VersionUpdater;
import org.jetbrains.annotations.NotNull;

/**
 * 检测更新
 *
 * @autho zhangquan
 */
public class CheckUpdateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        VersionUpdater.VersionUpdateResult result = VersionUpdater.checkUpdate(project);
        if (result != VersionUpdater.VersionUpdateResult.PLUGIN_NEW) {
            Messages.showInfoMessage(result.desc, "提示");
        }
    }
}
