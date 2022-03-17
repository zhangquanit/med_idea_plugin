package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.MyNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 切换源码依赖
 *
 * @autho zhangquan
 */
public class SourceCodeSwitchAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        File configFile = new File(MedConfig.getSourceConfigFilePath(project));
        if (!configFile.exists()) {
            Messages.showWarningDialog(MedConfig.SOURCE_CONFIG_NOT_EXIST, "提示");
            return;
        }
        MedUtil.openFileInEditor(project, MedConfig.getSourceConfigFile(project));
        MyNotifier.notify(project, "源码依赖配置文件已打开");
    }
}
