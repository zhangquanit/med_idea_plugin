package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.medlinker.idea.plugin.ui.ScriptExecutor;
import org.jetbrains.annotations.NotNull;

/**
 * 构建正式环境apk
 *
 * @autho zhangquan
 */
public class BuildOnlineAppAction extends AnAction {
    ScriptExecutor mScriptExecutor;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (null == mScriptExecutor) {
            mScriptExecutor = new ScriptExecutor();
        }
        mScriptExecutor.buildApp(project, true);
    }
}
