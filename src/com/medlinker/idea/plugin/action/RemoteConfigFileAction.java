package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.MyNotifier;

/**
 * 打开远程仓库文件
 */
public class RemoteConfigFileAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (!MedUtil.isRemoteConfigLibDirExist(project)) {
            Messages.showWarningDialog(MedConfig.REMOTE_CONFIG_NOT_EXIST, "提示");
            return;
        }
        MedUtil.openFileInEditor(project, MedConfig.getRemoteConfigLibFile(project));
        MyNotifier.notify(project, "远程仓库依赖文件已打开");

    }
}