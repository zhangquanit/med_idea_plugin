package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.medlinker.idea.plugin.util.MedUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 本地依赖库文件-检测更新
 *
 * @autho zhangquan
 */
public class LocalConfigFileAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        MedUtil.checkShowDiffAction(project);
    }
}
